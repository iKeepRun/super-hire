package com.zack.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.domain.Users;
import com.zack.dto.LoginDTO;
import com.zack.enums.Sex;
import com.zack.enums.ShowWhichName;
import com.zack.enums.UserRole;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.mapper.UsersMapper;
import com.zack.service.UsersService;
import com.zack.utils.IPUtil;
import com.zack.utils.JWTUtils;
import com.zack.utils.RedisOperator;
import com.zack.vo.SaasUserVO;
import com.zack.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description:企业后台接口
 * @param:
 * @return:
 **/
@RestController
@RequestMapping("/saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UsersService usersService;
    /**
     * @description: 获得二维码令牌
     * @param: []
     * @return: com.zack.common.CommonResult<java.lang.String>
     **/
    @PostMapping("/getQRToken")
    public CommonResult<String> getQRToken() {
        //生成扫码登陆token
        String qrToken = UUID.randomUUID().toString();
        //将token存入redis
        redisOperator.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5 * 60);
        //是否已读
        redisOperator.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5 * 60);
        return CommonResult.success(qrToken);
    }

    /**
     * @description: 手机端使用HR角色进行扫码登陆
     * @param: [qrToken, request]
     * @return: com.zack.common.CommonResult<java.lang.String>
     **/
    @PostMapping("/scanCode")
    public CommonResult<String> scanCode(String qrToken, HttpServletRequest request) {
        // 判空 - qrToken
        if (StringUtils.isBlank(qrToken))
            return CommonResult.error(ErrorCode.PARAMS_ERROR);

        // 从redis中获得并且判断qrToken是否有效
        String redisQRToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);
        if (!qrToken.equalsIgnoreCase(redisQRToken))
            return CommonResult.error(ErrorCode.PARAMS_ERROR);

        // 从header中获得用户id和jwt令牌
        String headerUserId = request.getHeader("appUserId");
        String headerUserToken = request.getHeader("appUserToken");

        // 判空 - headerUserId + headerUserToken
        if (StringUtils.isBlank(headerUserId) || StringUtils.isBlank(headerUserToken))
            return CommonResult.error(ErrorCode.HR_TICKET_INVALID);

        // 对JWT进行校验
        String userJson = jwtUtils.checkJWT(headerUserToken.split("@")[1]);
        if (StringUtils.isBlank(userJson))
            return CommonResult.error(ErrorCode.HR_TICKET_INVALID);

        // 执行后续正常业务
        // 生成预登录token
        String preToken = UUID.randomUUID().toString();
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, preToken, 5*60);

        // redis写入标记，当前qrToken需要被读取并且失效覆盖，网页端标记二维码已被扫
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "1," + preToken, 5*60);

        // 返回给手机端，app下次请求携带preToken
        return CommonResult.success(preToken);
    }



    /**
     * 3. SAAS网页端每隔一段时间（3秒）定时查询qrToken是否被读取，用于页面的展示标记判断
     * 前端处理：限制用户在页面不操作而频繁发起调用：【页面失效，请刷新后再执行扫描登录！】
     * 注：如果使用websocket或者netty，可以在app扫描之后，在上一个接口，直接通信浏览器（H5）进行页面扫码的状态标记
     * @param qrToken
     * @return
     */
    @PostMapping("codeHasBeenRead")
    public CommonResult codeHasBeenRead(String qrToken) {

        String readStr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        List list = new ArrayList();

        if (StringUtils.isNotBlank(readStr)) {
            String[] readArr = readStr.split(",");
            if (readArr.length >= 2) {
                list.add(Integer.valueOf(readArr[0]));
                list.add(readArr[1]);
            } else {
                list.add(0);
            }
            return CommonResult.success(list);
        } else {
            return CommonResult.success(list);
        }
    }

    /**
     * 4. 手机端点击却登录，携带preToken与后端进行判断，如果校验ok则登录成功
     * 注：如果使用websocket或者netty，可以在此直接通信H5进行页面的跳转
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
    @PostMapping("goQRLogin")
    public CommonResult goQRLogin(String userId,
                                     String qrToken,
                                     String preToken) {

        String preTokenRedisArr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);

        if (StringUtils.isNotBlank(preTokenRedisArr)) {
            String preTokenRedis = preTokenRedisArr.split(",")[1];
            if (preTokenRedis.equalsIgnoreCase(preToken)) {
                // 根据用户id获得用户信息
                Users hrUser = usersService.getById(userId);
                if (hrUser == null) {
                    return CommonResult.error(ErrorCode.USER_NOT_EXIST_ERROR);
                }

                // 存入用户信息到redis中，因为H5在未登录的情况下，拿不到用户id，所以暂存用户信息到redis。
                // *如果使用websocket是可以直接通信H5获得用户id，则无此问题
                redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken, new Gson().toJson(hrUser),5*60);
            }
        }

        return CommonResult.success();
    }

    /**
     * 5. 页面登录跳转
     * @param preToken
     * @return
     */
    @PostMapping("checkLogin")
    public CommonResult checkLogin(String preToken) {

        if (StringUtils.isBlank(preToken))
            return CommonResult.success("");

        // 获得用户临时信息
        String userJson = redis.get(REDIS_SAAS_USER_INFO + ":temp:" + preToken);

        if (StringUtils.isBlank(userJson))
            return CommonResult.error(ErrorCode.USER_NOT_EXIST_ERROR);

        // 确认执行登录后，生成saas用户的token，并且长期有效
        String saasUserToken = jwtUtils.createJWTWithPrefix(userJson, TOKEN_SAAS_PREFIX);

        // 存入用户信息，长期有效
        redis.set(REDIS_SAAS_USER_INFO + ":" + saasUserToken, userJson);

        return CommonResult.success(saasUserToken);
    }

    @GetMapping("info")
    public CommonResult info(String token) {

        String userJson = redis.get(REDIS_SAAS_USER_INFO + ":" + token);
        Users saasUser = new Gson().fromJson(userJson, Users.class);

        SaasUserVO saasUserVO = new SaasUserVO();
        BeanUtils.copyProperties(saasUser, saasUserVO);

        return CommonResult.success(saasUserVO);
    }

    @PostMapping("logout")
    public CommonResult logout(String token) {
        return CommonResult.success();
    }
}

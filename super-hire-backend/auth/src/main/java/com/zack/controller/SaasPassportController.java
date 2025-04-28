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
import com.zack.utils.IPUtil;
import com.zack.utils.JWTUtils;
import com.zack.utils.RedisOperator;
import com.zack.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
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
}

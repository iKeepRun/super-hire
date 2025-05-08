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
import com.zack.mq.MQConfig;
import com.zack.mq.SMSContentQO;
import com.zack.utils.GsonUtils;
import com.zack.utils.IPUtil;
import com.zack.utils.JWTUtils;
import com.zack.utils.RedisOperator;
import com.zack.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

/**
 * @description:App端注册登录接口
 * @param:
 * @return:
 **/
@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/getSMSCode")
    public CommonResult getSMSCode(String mobile,
                                   HttpServletRequest request) {
        ThrowUtil.throwIf(StrUtil.isBlank(mobile), ErrorCode.PARAMS_ERROR);
        String ip = IPUtil.getRequestIp(request);
        log.info("用户ip:{}", ip);
        // 用于防止频繁的访问短信接口
//        redisOperator.setnx60s(ip, mobile);
        SMSContentQO smsContentQO=new SMSContentQO();
        smsContentQO.setMobile(mobile);
        smsContentQO.setContent("1234");
        //TODO  对接第三方短信平台
          //使用mq异步解耦短信发送
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_NAME,MQConfig.ROUTING_KEY, GsonUtils.object2String(smsContentQO));

        //存储手机验证码
        redisOperator.set(mobile, "1234", 60L);

        return CommonResult.success();
    }

    @PostMapping("/login")
    public CommonResult<UsersVO> login(@Valid @RequestBody LoginDTO loginDTO,
                              HttpServletRequest request) {
        String mobile = loginDTO.getMobile();
        String code = loginDTO.getSmsCode();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redisOperator.get(mobile);
        if (StrUtil.isBlank(redisCode)) {
            return CommonResult.error("验证码已过期");
        }

        if (!redisCode.equalsIgnoreCase(code)) {
            return CommonResult.error("验证码不正确");
        }


        Users users = usersMapper.selectOne(new QueryWrapper<Users>().eq("mobile", mobile));
        // 用户不存在，就注册
        if (users == null) {
            users = new Users();
            String mobilePhone = DesensitizedUtil.mobilePhone(mobile);
            users.setMobile(mobilePhone);
            users.setNickname("昵称:"+mobilePhone);
            users.setReal_name("真名:"+mobilePhone);
            users.setShow_which_name(ShowWhichName.nickname.type);
            users.setSex(Sex.man.type);
            users.setFace("");
            users.setEmail("xxx@163.com");
            users.setBirthday(new Date());
            users.setCountry("中国");
            users.setProvince("");
            users.setCity("");
            users.setDistrict("");
            users.setDescription("这家伙喜欢coding,不喜欢说话");
            users.setStart_work_date(new Date());
            users.setPosition("底层码农");
            users.setRole(UserRole.CANDIDATE.type);
            users.setHr_in_which_company_id("");
            users.setHr_signature("");
            users.setHr_tags("");
            users.setCreated_time(new Date());
            users.setUpdated_time(new Date());

           usersMapper.insert(users);
        }

        String jwt = jwtUtils.createJWTWithPrefix(new Gson().toJson(users), TOKEN_USER_PREFIX);
        UsersVO usersVO = BeanUtil.copyProperties(users, UsersVO.class);
        usersVO.setUserToken(jwt);
        return CommonResult.success(usersVO);
    }

    @PostMapping("logout")
    public CommonResult logout(@RequestParam String userId,
                                  HttpServletRequest request) throws Exception {

        // 后端只需要清除用户的token信息即可，前端也需要清除相关的用户信息
//        redis.del(REDIS_USER_TOKEN + ":" + userId);

        return CommonResult.success();
    }
}

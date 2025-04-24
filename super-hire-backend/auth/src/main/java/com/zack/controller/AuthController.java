package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.utils.IPUtil;
import com.zack.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private RedisOperator redisOperator;

    @RequestMapping("/hello")
    public String hello(){
        return "hello auth";
    }

    @GetMapping("/getSMSCode")
    public CommonResult getSMSCode(String mobile,
                                   HttpServletRequest request){
        ThrowUtil.throwIf(StrUtil.isBlank(mobile), ErrorCode.PARAMS_ERROR);
        String ip = IPUtil.getRequestIp(request);
        // 用于防止频繁的访问短信接口
        redisOperator.setnx60s(ip,mobile);
        //TODO  对接第三方短信平台
        //存储手机验证码
        redisOperator.set(mobile, "1234",60L);

        return  CommonResult.success();
    }
}

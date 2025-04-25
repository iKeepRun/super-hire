package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.utils.IPUtil;
import com.zack.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {


    @RequestMapping("/hello")
    public String hello(){
        return "hello auth";
    }

}

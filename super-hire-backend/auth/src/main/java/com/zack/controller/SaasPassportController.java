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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/sass")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UsersMapper usersMapper;

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


}

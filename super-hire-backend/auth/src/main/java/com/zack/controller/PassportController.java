package com.zack.controller;
import java.util.Date;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zack.common.CommonResult;
import com.zack.domain.Users;
import com.zack.dto.LoginDTO;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.mapper.UsersMapper;
import com.zack.utils.IPUtil;
import com.zack.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController {
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UsersMapper usersMapper;

    @PostMapping("/getSMSCode")
    public CommonResult getSMSCode(String mobile,
                                   HttpServletRequest request) {
        ThrowUtil.throwIf(StrUtil.isBlank(mobile), ErrorCode.PARAMS_ERROR);
        String ip = IPUtil.getRequestIp(request);
        log.info("用户ip:{}", ip);
        // 用于防止频繁的访问短信接口
//        redisOperator.setnx60s(ip, mobile);
        //TODO  对接第三方短信平台
        //存储手机验证码
        redisOperator.set(mobile, "1234", 60L);

        return CommonResult.success();
    }

    @PostMapping("/login")
    public CommonResult login(@Valid @RequestBody LoginDTO loginDTO,
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
            Users user = new Users();
            user.setMobile(mobile);
            String name = mobile + "_" + NumberUtil.roundStr(Math.random() * 1000000, 6);
            user.setNickname(name);
            user.setReal_name(name);
            user.setShow_which_name(1);
            user.setSex(1);
            user.setFace("");
            user.setEmail("");
            user.setBirthday(new Date());
            user.setCountry("");
            user.setProvince("");
            user.setCity("");
            user.setDistrict("");
            user.setDescription("");
            user.setStart_work_date(new Date());
            user.setPosition("");
            user.setRole(0);
            user.setHr_in_which_company_id("");
            user.setHr_signature("");
            user.setHr_tags("");
            user.setCreated_time(new Date());
            user.setUpdated_time(new Date());


        }
        return CommonResult.success();
    }

}

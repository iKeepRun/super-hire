package com.zack.controller;
import java.util.Date;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
            users = new Users();
            String mobilePhone = DesensitizedUtil.mobilePhone(mobile);
            users.setMobile(mobilePhone);
            String name = mobilePhone + "_" + NumberUtil.roundStr(Math.random() * 1000000, 6);
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
        return CommonResult.success(users);
    }

}

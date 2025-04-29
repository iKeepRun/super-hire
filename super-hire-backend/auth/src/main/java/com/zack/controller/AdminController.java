package com.zack.controller;

import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.domain.Admin;
import com.zack.dto.AdminDTO;
import com.zack.exceptions.ErrorCode;
import com.zack.inteceptor.JwtCurrentUserInteceptor;
import com.zack.service.AdminService;
import com.zack.utils.JWTUtils;
import com.zack.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("admin")
@Slf4j
public class AdminController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("login")
    public CommonResult getSMSCode(@Valid @RequestBody AdminDTO adminDTO) {

        // 执行登录判断用户是否存在
        boolean isExist = adminService.adminLogin(adminDTO);
        if (!isExist)
            return CommonResult.error(
                    ErrorCode.ADMIN_LOGIN_ERROR);

        // 登录成功之后获得admin信息
        Admin admin = adminService.getAdminInfo(adminDTO);
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(admin),
                TOKEN_ADMIN_PREFIX);

        return CommonResult.success(adminToken);
    }

    @GetMapping("info")
    public CommonResult info() {

        Admin admin = JwtCurrentUserInteceptor.adminUser.get();

        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);

        return CommonResult.success(adminVO);
    }

    @PostMapping("logout")
    public CommonResult logout() {
        return CommonResult.success();
    }

}

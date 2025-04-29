package com.zack.controller;


import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.domain.Admin;
import com.zack.dto.CreateAdminDTO;
import com.zack.dto.ResetPwdDTO;
import com.zack.dto.UpdateAdminDTO;
import com.zack.inteceptor.JwtCurrentUserInteceptor;
import com.zack.service.AdminService;
import com.zack.vo.AdminInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @PostMapping("create")
    public CommonResult create(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        adminService.createAdmin(createAdminDTO);
        return CommonResult.success();
    }

    @PostMapping("list")
    public CommonResult<CommonPage> list(String accountName,
                                         Integer page,
                                         Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        CommonPage listResult = adminService.getAdminList(accountName,
                page,
                limit);

        return CommonResult.success(listResult);
    }

    @PostMapping("delete")
    public CommonResult delete(String username) {
        adminService.deleteAdmin(username);
        return CommonResult.success();
    }

    @PostMapping("resetPwd")
    public CommonResult resetPwd(@RequestBody ResetPwdDTO resetPwdDTO) {

        // resetPwdDTO 校验
        // adminService 重置密码

        resetPwdDTO.modifyPwd();
        return CommonResult.success();
    }

    @PostMapping("myInfo")
    public CommonResult myInfo() {
        Admin admin = JwtCurrentUserInteceptor.adminUser.get();
        Admin adminInfo = adminService.getById(admin.getId());

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(adminInfo, adminInfoVO);

        return CommonResult.success(adminInfoVO);
    }

    @PostMapping("updateMyInfo")
    public CommonResult updateMyInfo(@RequestBody @Valid UpdateAdminDTO adminDTO) {
        Admin admin = JwtCurrentUserInteceptor.adminUser.get();
        adminDTO.setId(admin.getId());
        adminService.updateAdmin(adminDTO);
        return CommonResult.success();
    }

}


package com.zack.feign;

import com.zack.common.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 简历模块客户端
 */

@FeignClient("user-service")
public interface UserInfoMicroFeign {
    @PostMapping("/userinfo/getCountsByCompanyId")
    CommonResult getCountsByCompanyId(
            @RequestParam("companyId") String companyId);

    @PostMapping("/userinfo/bindingHRToCompany")
     CommonResult bindingHRToCompany(
            @RequestParam("hrUserId") String hrUserId,
            @RequestParam("realname") String realname,
            @RequestParam("companyId") String companyId);
}

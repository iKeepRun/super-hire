package com.zack.feign;

import com.zack.bo.SearchBO;
import com.zack.common.CommonResult;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("company-service")
public interface CompanyMicroServiceFeign {
    @PostMapping("/company/list/get")
    public CommonResult getList(@RequestBody SearchBO searchBO);
}

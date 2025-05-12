package com.zack.feign;

import com.zack.common.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 简历模块客户端
 */

@FeignClient("work-service")
public interface WorkMicroFeign {
    @PostMapping("/resume/init")
    CommonResult init(@RequestParam("userId") String userId);
}

package com.zack.controller;

import com.zack.common.CommonResult;
import com.zack.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume")
public class ResumeController {
   @Autowired
   private ResumeService resumeService;
    @PostMapping("/init")
    public CommonResult init(String userId ){
        resumeService.initResume(userId);
        return CommonResult.success();
    }
}

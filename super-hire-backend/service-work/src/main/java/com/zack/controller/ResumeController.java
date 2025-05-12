package com.zack.controller;

import com.zack.common.CommonResult;
import com.zack.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
public class ResumeController {
   @Autowired
   private ResumeService resumeService;
    @PostMapping("/init")
    public CommonResult init(@RequestParam("userId") String userId ){
        resumeService.initResume(userId);
        return CommonResult.success();
    }

    @GetMapping("/hello")
    public String update(){
        return "dada";
    }
}

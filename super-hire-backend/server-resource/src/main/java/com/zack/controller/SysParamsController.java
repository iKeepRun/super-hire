package com.zack.controller;

import com.zack.common.CommonResult;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys")
public class SysParamsController {
    @Autowired
    private SysParamsService sysParamsService;
    @PostMapping("/modifyMaxResumeRefreshCounts")
    public CommonResult modifyMaxResumeRefreshCounts(Integer maxCounts,Integer version){
        //参数校验
        ThrowUtil.throwIf(maxCounts == null|| maxCounts<0, ErrorCode.PARAMS_ERROR);

        sysParamsService.updateMaxResumeRefreshCountsSysParams(maxCounts,version);

        return CommonResult.success();
    }
}

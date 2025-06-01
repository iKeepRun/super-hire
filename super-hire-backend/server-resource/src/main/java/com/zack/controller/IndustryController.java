package com.zack.controller;

import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.Industry;
import com.zack.service.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("industry")
public class IndustryController {
   @Autowired
   private IndustryService industryService;

    @PostMapping("createNode")
    public CommonResult createNode(@RequestBody Industry industry) {

        // 判断节点是否已经存在
        if (industryService.getIndustryIsExistByName(industry.getName()))
            return CommonResult.error("该行业已经存在！请重新命名~");

        // 节点创建
        industryService.createIndustry(industry);

//        resetRedisIndustry(industry);

        return CommonResult.success();
    }
}

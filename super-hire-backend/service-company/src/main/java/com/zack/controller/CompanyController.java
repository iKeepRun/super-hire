package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.Company;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.service.CompanyService;
import com.zack.vo.CompanySimpleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {
  @Autowired
  private CompanyService companyService;

    /**
     * 根据全称查询企业信息
     * @param fullName
     * @return
     */
    @PostMapping("getByFullName")
    public CommonResult getByFullName(String fullName) {

        ThrowUtil.throwIf(StrUtil.isBlank(fullName), ErrorCode.PARAMS_ERROR);

        Company company = companyService.getByFullName(fullName);
        if (company == null) return CommonResult.success(null);

        CompanySimpleVO companySimpleVO = new CompanySimpleVO();
        BeanUtils.copyProperties(company, companySimpleVO);

        return CommonResult.success(companySimpleVO);
    }
}

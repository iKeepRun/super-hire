package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.bo.CreateCompanyBO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    /**
     * 创建待审核的企业或者重新发起审核
     * @param createCompanyBO
     * @return
     */
    @PostMapping("createNewCompany")
    public CommonResult createNewCompany(
            @RequestBody @Valid CreateCompanyBO createCompanyBO) {
        // TODO 课后自行校验 CreateCompanyBO
        String companyId = createCompanyBO.getCompanyId();
        String doCompanyId = "";
        if (StrUtil.isBlank(companyId)) {
            // 如果为空，则创建公司
            doCompanyId = companyService.createNewCompany(createCompanyBO);
        } else {
            // 否则不为空，则在原有的公司信息基础上做修改
            doCompanyId = companyService.resetNewCompany(createCompanyBO);
        }

        return CommonResult.success(doCompanyId);
    }
}

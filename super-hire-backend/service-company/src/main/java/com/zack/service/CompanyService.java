package com.zack.service;

import com.zack.bo.CreateCompanyBO;
import com.zack.domain.Company;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author chenzhiqiang
* @description 针对表【company(企业表)】的数据库操作Service
* @createDate 2025-05-17 10:19:37
*/
public interface CompanyService {
    /**
     * 根据企业全称查询企业信息
     * @param fullName
     * @return
     */
    public Company getByFullName(String fullName);
    String resetNewCompany(CreateCompanyBO createCompanyBO);
    String createNewCompany(CreateCompanyBO createCompanyBO);
    Company getById(String id);
}

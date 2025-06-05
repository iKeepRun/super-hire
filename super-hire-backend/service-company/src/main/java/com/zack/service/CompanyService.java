package com.zack.service;

import com.zack.bo.CreateCompanyBO;
import com.zack.bo.QueryCompanyBO;
import com.zack.bo.ReviewCompanyBO;
import com.zack.common.CommonPage;
import com.zack.domain.Company;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.vo.CompanyInfoVO;

/**
 * @author chenzhiqiang
 * @description 针对表【company(企业表)】的数据库操作Service
 * @createDate 2025-05-17 10:19:37
 */
public interface CompanyService {
    /**
     * 根据企业全称查询企业信息
     *
     * @param fullName
     * @return
     */
    Company getByFullName(String fullName);

    String resetNewCompany(CreateCompanyBO createCompanyBO);

    String createNewCompany(CreateCompanyBO createCompanyBO);

    Company getById(String id);

    void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO);

    CommonPage<CompanyInfoVO> queryCompanyList(QueryCompanyBO companyBO,
                                               Integer page,
                                               Integer limit);
}

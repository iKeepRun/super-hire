package com.zack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.domain.Company;
import com.zack.service.CompanyService;
import com.zack.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenzhiqiang
 * @description 针对表【company(企业表)】的数据库操作Service实现
 * @createDate 2025-05-17 10:19:37
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public Company getByFullName(String fullName) {
        Company tempCompany = companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName)
        );

        return tempCompany;
    }
}





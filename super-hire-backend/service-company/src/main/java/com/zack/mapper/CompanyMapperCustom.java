package com.zack.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zack.bo.QueryCompanyBO;
import com.zack.common.CommonPage;
import com.zack.domain.Company;
import com.zack.vo.CompanyInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【company(企业表)】的数据库操作Mapper
* @createDate 2025-05-17 10:19:37
* @Entity com.zack.domain.Company
*/
public interface CompanyMapperCustom  {

    List<CompanyInfoVO> queryCompanyList(@Param("paramMap") Map map);
}





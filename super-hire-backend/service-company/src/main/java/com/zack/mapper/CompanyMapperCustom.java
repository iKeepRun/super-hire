package com.zack.mapper;

import com.zack.vo.CompanyInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【company(企业表)】的数据库操作Mapper
* @createDate 2025-05-17 10:19:37
* @Entity com.zack.domain.Company
*/
@Mapper
public interface CompanyMapperCustom  {

    List<CompanyInfoVO> queryCompanyList(@Param("paramMap") Map<String,Object> map);

    CompanyInfoVO getCompanyInfo(@Param("paramMap") Map<String,Object> map);

}





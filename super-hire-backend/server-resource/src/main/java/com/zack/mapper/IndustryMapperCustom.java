package com.zack.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zack.domain.Industry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【industry(行业表)】的数据库操作Mapper
* @createDate 2025-05-17 10:19:37
* @Entity com.zack.domain.Industry
*/

@Mapper
public interface IndustryMapperCustom{
    List<Industry> getThirdIndustryByTop(@Param("paramMap") Map<String,Object> map);
}





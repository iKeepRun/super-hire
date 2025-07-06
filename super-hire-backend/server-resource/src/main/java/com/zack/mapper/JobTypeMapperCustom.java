package com.zack.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zack.domain.JobType;
import com.zack.vo.JobTypeSecondAndThirdVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【job_type(职位类别)】的数据库操作Mapper
* @createDate 2025-05-17 10:19:37
* @Entity com.zack.domain.JobType
*/
@Mapper
public interface JobTypeMapperCustom extends BaseMapper<JobType> {

    public List<JobType> getThirdJobTypeByTop(@Param("paramMap") Map<String, Object> map);

    public List<JobTypeSecondAndThirdVO> getSecondAndThirdListByTop(@Param("paramMap") Map<String, Object> map);

}





package com.zack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.base.BaseInfoProperties;
import com.zack.domain.JobType;
import com.zack.mapper.JobTypeMapper;
import com.zack.mapper.JobTypeMapperCustom;
import com.zack.service.JobTypeService;
import com.zack.vo.JobTypeSecondAndThirdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【job_type(职位类别)】的数据库操作Service实现
* @createDate 2025-05-17 10:19:37
*/
@Service
public class JobTypeServiceImpl extends BaseInfoProperties implements JobTypeService {

    @Autowired
    private JobTypeMapper jobTypeMapper;

    @Autowired
    private JobTypeMapperCustom jobTypeMapperCustom;

    @Override
    public List<JobType> getTopJobTypeList() {
        List<JobType> topJobTypeList = jobTypeMapper.selectList(
                new QueryWrapper<JobType>()
                        .eq("father_id", 0)
                        .orderByAsc("sort")
        );

        return topJobTypeList;
    }

    @Override
    public List<JobType> getThirdListByTop(String topJobTypeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("topJobTypeId", topJobTypeId);
        List<JobType> jobTypeList = jobTypeMapperCustom.getThirdJobTypeByTop(map);

        return jobTypeList;
    }

    @Override
    public List<JobTypeSecondAndThirdVO> getSecondAndThirdListByTop(String topJobTypeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("topJobTypeId", topJobTypeId);
        List<JobTypeSecondAndThirdVO> list = jobTypeMapperCustom.getSecondAndThirdListByTop(map);

        return list;
    }

    @Override
    public List<JobType> getChildrenJobTypeList(String jobTypeId) {
        List<JobType> topJobTypeList = jobTypeMapper.selectList(
                new QueryWrapper<JobType>()
                        .eq("father_id", jobTypeId)
                        .orderByAsc("sort")
        );

        return topJobTypeList;
    }

    @Override
    public Long getChildrenJobTypeCounts(String jobTypeId) {
        Long counts = jobTypeMapper.selectCount(
                new QueryWrapper<JobType>()
                        .eq("father_id", jobTypeId)
        );
        return counts;
    }

    @Override
    public boolean getJobTypeIsExistByName(String name) {
        JobType jobType = jobTypeMapper.selectOne(new QueryWrapper<JobType>()
                .eq("name", name));

        return jobType != null ? true : false;
    }

    @Override
    public JobType getJobTypeById(String id) {
        return jobTypeMapper.selectById(id);
    }

    @Override
    public void createJobType(JobType jobType) {
        jobTypeMapper.insert(jobType);
    }

    @Override
    public void updateJobType(JobType jobType) {
        jobTypeMapper.updateById(jobType);
    }

    @Override
    public void deleteJobType(String id) {
        jobTypeMapper.deleteById(id);
    }
}






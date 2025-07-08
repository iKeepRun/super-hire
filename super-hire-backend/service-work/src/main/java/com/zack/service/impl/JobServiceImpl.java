package com.zack.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.EditJobBO;
import com.zack.bo.SearchBO;
import com.zack.bo.SearchJobsBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.domain.Job;
import com.zack.enums.JobStatus;
import com.zack.feign.CompanyMicroServiceFeign;
import com.zack.feign.UserInfoMicroFeign;
import com.zack.mapper.JobMapper;
import com.zack.service.JobService;
import com.zack.utils.GsonUtils;
import com.zack.vo.CompanyInfoVO;
import com.zack.vo.SearchJobsVO;
import com.zack.vo.UsersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @author chenzhiqiang
* @description 针对表【job(HR发布的职位表)】的数据库操作Service实现
* @createDate 2025-05-17 10:19:37
*/
@Service
public class JobServiceImpl extends BaseInfoProperties implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private UserInfoMicroFeign userInfoMicroFeign;

    @Autowired
    private CompanyMicroServiceFeign companyMicroServiceFeign;

    @Transactional
    @Override
    public void modifyJobDetail(EditJobBO editJobBO) {

        Job job = new Job();
        BeanUtils.copyProperties(editJobBO, job);

        job.setUpdatedTime(LocalDateTime.now());

        if (StrUtil.isBlank(editJobBO.getId())) {
            // 新增
            job.setStatus(JobStatus.OPEN.type);
            job.setCreateTime(LocalDateTime.now());
            jobMapper.insert(job);
        } else {
            // 修改
            jobMapper.update(job, new QueryWrapper<Job>()
                    .eq("id", editJobBO.getId())
                    .eq("hr_id", editJobBO.getHrId())
                    .eq("company_id", editJobBO.getCompanyId())
            );
        }

        redis.del(REDIS_JOB_DETAIL +
                ":" + editJobBO.getCompanyId() +
                ":" + editJobBO.getHrId() +
                ":" + editJobBO.getId()
        );
    }

    @Override
    public CommonPage<Job> queryJobList(String hrId,
                                        String companyId,
                                        Integer page,
                                        Integer pageSize,
                                        Integer status) {
        PageHelper.startPage(page, pageSize);

        QueryWrapper queryWrapper = new QueryWrapper<Job>();
        if (StrUtil.isNotBlank(hrId)) {
            queryWrapper.eq("hr_id", hrId);
        }

        queryWrapper.eq("company_id", companyId);

        if (status != null) {
            if (status == JobStatus.OPEN.type ||
                    status == JobStatus.CLOSE.type ||
                    status == JobStatus.DELETE.type) {
                queryWrapper.eq("status", status);
            }
        }

        queryWrapper.orderByDesc("updated_time");

        List<Job> jobList = jobMapper.selectList(queryWrapper);
        return setPage(jobList, page);
    }

    @Override
    public Job queryJobDetail(String hrId, String companyId, String jobId) {
        Integer[] status = new Integer[]{
                JobStatus.OPEN.type,
                JobStatus.CLOSE.type,
                JobStatus.DELETE.type
        };

        QueryWrapper queryWrapper = new QueryWrapper<Job>();
        queryWrapper.eq("id", jobId);

        // 为啥要增加判断？因为增加判断可以提高扩展性，更加灵活
        // 可以让admin/company来调用查询，只需要增加接口即可，提高service的公用性
        if (StrUtil.isNotBlank(hrId)) {
            queryWrapper.eq("hr_id", hrId);
        }
        if (StrUtil.isNotBlank(companyId)) {
            queryWrapper.eq("company_id", companyId);
        }

        queryWrapper.in("status", status);

        Job job = jobMapper.selectOne(queryWrapper);

        redis.set(REDIS_JOB_DETAIL +
                ":" + companyId +
                ":" + hrId +
                ":" + jobId, GsonUtils.object2String(job)
        );
        return job;
    }

    @Transactional
    @Override
    public void modifyJobStatus(String hrId,
                                String companyId,
                                String jobId,
                                JobStatus jobStatus) {

        Job job = new Job();
        job.setStatus(jobStatus.type);
        job.setUpdatedTime(LocalDateTime.now());

        // 修改
        jobMapper.update(job, new QueryWrapper<Job>()
                .eq("id", jobId)
                .eq("hr_id", hrId)
                .eq("company_id", companyId)
        );

        redis.del(REDIS_JOB_DETAIL +
                ":" + companyId +
                ":" + hrId +
                ":" + jobId
        );
    }

    @Override
    public CommonPage searchJobs(SearchJobsBO searchJobsBO,
                                 Integer page,
                                 Integer pageSize) {

        String jobName = searchJobsBO.getJobName();
        String jobType = searchJobsBO.getJobType();
        String city = searchJobsBO.getCity();
        Integer beginSalary = searchJobsBO.getBeginSalary();
        Integer endSalary = searchJobsBO.getEndSalary();

        PageHelper.startPage(page, pageSize);

        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", JobStatus.OPEN.type);

        if (StrUtil.isNotBlank(jobName)) {
            queryWrapper.like("job_name", jobName);
        }

        if (StrUtil.isNotBlank(jobType)) {
            queryWrapper.like("job_type", jobType);
        }

        if (StrUtil.isNotBlank(city)) {
            queryWrapper.like("city", city);
        }

        if (beginSalary > 0 && endSalary > 0) {
//            queryWrapper.ge("end_salary", beginSalary);
            // 优化薪资区间的查询
            queryWrapper.and(
                    qw -> qw.or(
                                    // 职位最低薪资begin <= 求职薪资begin <= 职位最高薪资end
                                    subQW -> subQW.ge("end_salary", beginSalary)
                                            .le("begin_salary", beginSalary)
                            )
                            .or(
                                    // 职位最低薪资begin <= 求职薪资end <= 职位最高薪资end
                                    subQW -> subQW.ge("end_salary", endSalary)
                                            .le("begin_salary", endSalary)
                            )
                            .or(
                                    // 求职薪资begin <= 职位最低薪资begin  求职薪资end >= 职位最高薪资end
                                    subQW -> subQW.ge("begin_salary", beginSalary)
                                            .le("end_salary", endSalary)
                            )
            );
        }

        List<Job> jobList = jobMapper.selectList(queryWrapper);

        // 为空则不需要执行后续的数据查询拼接操作了
        if (jobList == null || jobList.isEmpty() || jobList.size() == 0) {
            return setPage(jobList, page);
        }

        // 根据每个job中的企业id，去获得企业信息
        List<String> companyIds = new ArrayList<>();
        // 根据每个job中的hrid，去获得hr用户信息
        List<String> hrIds = new ArrayList<>();

        // 构建VO对象，用户返回给前端
        List<SearchJobsVO> jobsVOList = new ArrayList<>();
        for (Job j : jobList) {
            hrIds.add(j.getHrId());
            companyIds.add(j.getCompanyId());

            SearchJobsVO searchJobsVO = new SearchJobsVO();
            BeanUtils.copyProperties(j, searchJobsVO);
            jobsVOList.add(searchJobsVO);
        }

        // 远程调用查询并且拼接hr用户信息
        SearchBO searchBO = new SearchBO();
        searchBO.setUserIds(hrIds);
        CommonResult userResult = userInfoMicroFeign.getList(searchBO);
        String userListStr = (String)userResult.getData();
        List<UsersVO> hrUsersList = GsonUtils.stringToListAnother(userListStr, UsersVO.class);

        for (SearchJobsVO j : jobsVOList) {
            for (UsersVO u : hrUsersList) {
                if (j.getHrId().equals(u.getId())) {
                    j.setUsersVO(u);
                }
            }
        }

        // 远程调用查询并且拼接企业信息
        searchBO.setCompanyIds(companyIds);
        CommonResult companyResult = companyMicroServiceFeign.getList(searchBO);
        String companyListStr = (String)companyResult.getData();
        List<CompanyInfoVO> companyInfoVOList = GsonUtils.stringToListAnother(companyListStr, CompanyInfoVO.class);

        for (SearchJobsVO j : jobsVOList) {
            for (CompanyInfoVO c : companyInfoVOList) {
                if (j.getCompanyId().equals(c.getCompanyId())) {
                    j.setCompanyInfoVO(c);
                }
            }
        }

        CommonPage gridResult = setPage(jobList, page);
        gridResult.setRows(jobsVOList);

        return gridResult;
    }
}






package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.EditJobBO;
import com.zack.bo.SearchJobsBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.domain.Job;
import com.zack.domain.Users;
import com.zack.enums.JobStatus;
import com.zack.inteceptor.JwtCurrentUserInteceptor;
import com.zack.service.JobService;
import com.zack.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("job")
public class JobController extends BaseInfoProperties {

    @Autowired
    private JobService jobService;

    /**
     * 新增或者编辑职位
     * @param editJobBO
     * @return
     */
    @PostMapping("modify")
    public CommonResult modify(@RequestBody @Valid EditJobBO editJobBO) {

        // TODO EditJobBO 自行校验

        jobService.modifyJobDetail(editJobBO);

        return CommonResult.success();
    }

    /**
     * 分页查询职位列表
     * @param hrId
     * @param companyId
     * @param page
     * @param limit
     * @param status
     * @return
     */
    @PostMapping("hr/jobList")
    public CommonResult jobListHR(String hrId,
                                     String companyId,
                                     Integer page,
                                     Integer limit,
                                     Integer status) {

        if (StrUtil.isBlank(hrId)) {
            return CommonResult.error("hrId 不能为空");
        }

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        CommonPage gridResult = jobService.queryJobList(hrId,
                companyId,
                page,
                limit,
                status);

        return CommonResult.success(gridResult);
    }

    /**
     * 获得历史职位列表
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("jobList")
    public CommonResult jobListCompany(Integer page, Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        Users users = JwtCurrentUserInteceptor.currentUser.get();
        String companyId = users.getHrInWhichCompanyId();

        CommonPage gridResult = jobService.queryJobList(null,
                companyId,
                page,
                limit,
                null);

        return CommonResult.success(gridResult);
    }

    /**
     * 查询职位详情
     * @param hrId
     * @param companyId
     * @param jobId
     * @return
     */
    @PostMapping("hr/jobDetail")
    public CommonResult jobDetailHR(String hrId,
                                       String companyId,
                                       String jobId) {

        if (StrUtil.isBlank(hrId) || StrUtil.isBlank(companyId) || StrUtil.isBlank(jobId)) {
            return CommonResult.error("hrId, companyId, jobId 不能为空");
        }

        String jobDetailStr = redis.get(REDIS_JOB_DETAIL + ":" + companyId + ":" + hrId + ":" + jobId);
        Job job = null;
        if (StrUtil.isBlank(jobDetailStr)) {
            job = jobService.queryJobDetail(hrId, companyId, jobId);
        } else {
            job = GsonUtils.stringToBean(jobDetailStr, Job.class);
        }

        return CommonResult.success(job);
    }

    /**
     * 企业和admin查询职位详情
     * @param jobId
     * @return
     */
    @PostMapping("admin/jobDetail")
    public CommonResult jobDetailAdminOrCompany(String jobId) {

        if (StrUtil.isBlank(jobId)) {
            return CommonResult.error("jobId 不能为空");
        }

        Job job = jobService.queryJobDetail(null, null, jobId);

        return CommonResult.success(job);
    }

    /**
     * 关闭和开启职位分为两个不同接口，为何不写在一起？
     * 因为如果写在同一个接口，无疑我们需要传入status状态作为参数
     * 只要接口存在参数，那就会增加风险，可能会被黑客攻击
     * 而且按照目前的接口解耦规范，我们也需要分开作为两个不同的接口
     * 另外，有判断就会有计算的损耗
     */

    /**
     * 关闭职位
     * @param hrId
     * @param companyId
     * @param jobId
     * @return
     */
    @PostMapping("close")
    public CommonResult jobClose(String hrId, String companyId, String jobId) {

        if (StrUtil.isBlank(hrId) || StrUtil.isBlank(companyId) || StrUtil.isBlank(jobId)) {
            return CommonResult.error("hrId, companyId, jobId 不能为空");
        }

        jobService.modifyJobStatus(hrId, companyId, jobId, JobStatus.CLOSE);

        return CommonResult.success();
    }

    /**
     * 开放职位
     * @param hrId
     * @param companyId
     * @param jobId
     * @return
     */
    @PostMapping("open")
    public CommonResult jobOpen(String hrId, String companyId, String jobId) {

        if (StrUtil.isBlank(hrId) || StrUtil.isBlank(companyId) || StrUtil.isBlank(jobId)) {
            return CommonResult.error("hrId, companyId, jobId 不能为空");
        }

        jobService.modifyJobStatus(hrId, companyId, jobId, JobStatus.OPEN);

        return CommonResult.success();
    }

    /**
     * 候选人搜索职位
     * @param searchJobsBO
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("searchJobs")
    public CommonResult searchJobs(@RequestBody SearchJobsBO searchJobsBO,
                                      Integer page,
                                      Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        CommonPage gridResult = jobService.searchJobs(searchJobsBO, page, limit);

        return CommonResult.success(gridResult);
    }
}


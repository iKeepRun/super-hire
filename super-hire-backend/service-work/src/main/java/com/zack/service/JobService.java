package com.zack.service;

import com.zack.bo.EditJobBO;
import com.zack.bo.SearchJobsBO;
import com.zack.common.CommonPage;
import com.zack.domain.Job;
import com.zack.enums.JobStatus;

/**
* @author chenzhiqiang
* @description 针对表【job(HR发布的职位表)】的数据库操作Service
* @createDate 2025-05-17 10:19:37
*/
public interface JobService {

    /**
     * 编辑职位信息
     * @param editJobBO
     */
    public void modifyJobDetail(EditJobBO editJobBO);

    /**
     * 查询职位列表
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public CommonPage queryJobList(String hrId,
                                        String companyId,
                                        Integer page,
                                        Integer pageSize,
                                        Integer status);

    /**
     * 查询职位详情
     * @param hrId
     * @param companyId
     * @param jobId
     * @return
     */
    public Job queryJobDetail(String hrId, String companyId, String jobId);

    /**
     * 修改职位状态
     * @param hrId
     * @param companyId
     * @param jobId
     * @param jobStatus
     */
    public void modifyJobStatus(String hrId,
                                String companyId,
                                String jobId,
                                JobStatus jobStatus);

    /**
     * 搜索职位
     * @param searchJobsBO
     * @param page
     * @param limit
     * @return
     */
    public CommonPage searchJobs(SearchJobsBO searchJobsBO,
                                 Integer page,
                                 Integer pageSize);
}


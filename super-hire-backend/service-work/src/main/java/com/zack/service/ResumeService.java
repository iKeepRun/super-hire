package com.zack.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.bo.*;
import com.zack.domain.*;
import com.zack.utils.GsonUtils;
import com.zack.vo.ResumeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenzhiqiang
 * @description 针对表【resume(简历表)】的数据库操作Service
 * @createDate 2025-05-10 22:52:49
 */
public interface ResumeService {

    void initResume(String userId);

    public void modifyResume(EditResumeBO editResumeBO);


    public ResumeVO getResumeInfo(String userId);

    public void editWorkExp(EditWorkExpBO workExpBO);

    public ResumeWorkExp getWorkExp(String workExpId, String userId);

    public void deleteWorkExp(String workExpId, String userId);

    public void editProjectExp(EditProjectExpBO projectExpBO);

    public ResumeProjectExp getProjectExp(String projectExpId, String userId);

    public void deleteProjectExp(String projectExpId, String userId);

    public void editEducation(EditEducationBO educationBO);

    public ResumeEducation getEducation(String eduId, String userId);

    public void deleteEducation(String eduId, String userId);

    public void editJobExpect(EditResumeExpectBO expectBO);

    public List<ResumeExpect> getMyResumeExpectList(String resumeId, String userId);

    public void deleteResumeExpect(String resumeExpectId, String userId);

}

package com.zack.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.*;
import com.zack.domain.*;
import com.zack.mapper.*;
import com.zack.service.ResumeService;
import com.zack.utils.GsonUtils;
import com.zack.vo.ResumeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author chenzhiqiang
 * @description 针对表【resume(简历表)】的数据库操作Service实现
 * @createDate 2025-05-10 22:52:49
 */
@Service
public class ResumeServiceImpl extends BaseInfoProperties implements ResumeService {

    @Autowired
    private ResumeMapper resumeMapper;
    @Autowired
    private ResumeWorkExpMapper workExpMapper;
    @Autowired
    private ResumeProjectExpMapper projectExpMapper;
    @Autowired
    private ResumeEducationMapper educationMapper;
    @Autowired
    private ResumeExpectMapper expectMapper;
    @Override
    public void initResume(String userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());
        resumeMapper.insert(resume);
    }

    @Transactional
    @Override
    public void modifyResume(EditResumeBO editResumeBO) {

        Resume resume = new Resume();
        BeanUtils.copyProperties(editResumeBO, resume);

        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.update(resume,
                new QueryWrapper<Resume>()
                        .eq("id", editResumeBO.getId())
                        .eq("user_id", editResumeBO.getUserId())
        );

        redis.del(REDIS_RESUME_INFO + ":" + editResumeBO.getUserId());
    }

    @Override
    public ResumeVO getResumeInfo(String userId) {

        ResumeVO resumeVO = new ResumeVO();

        // 1. 查询简历信息
        Resume resume = resumeMapper.selectOne(
                new QueryWrapper<Resume>()
                        .eq("user_id", userId)
        );
        if (resume == null) return null;
        BeanUtils.copyProperties(resume, resumeVO);

        // 2. 查询工作经验
        List<ResumeWorkExp> workExpList = workExpMapper.selectList(
                new QueryWrapper<ResumeWorkExp>()
                        .eq("user_id", userId)
                        .eq("resume_id", resume.getId())
                        .orderByDesc("begin_date")
        );

        // 3. 查询项目经验
        List<ResumeProjectExp> projectExpList = projectExpMapper.selectList(
                new QueryWrapper<ResumeProjectExp>()
                        .eq("user_id", userId)
                        .eq("resume_id", resume.getId())
                        .orderByDesc("begin_date")
        );
        //
        // // 4. 查询我的教育经历
        // List<ResumeEducation> educationList = educationMapper.selectList(
        //         new QueryWrapper<ResumeEducation>()
        //                 .eq("user_id", userId)
        //                 .eq("resume_id", resume.getId())
        //                 .orderByDesc("begin_date")
        // );
        //
        // // 在这里不做多表关联查询，单独查表后再进行组装，避免高并发对数据库的压力
        resumeVO.setWorkExpList(workExpList);
        resumeVO.setProjectExpList(projectExpList);
        // resumeVO.setEducationList(educationList);

        return resumeVO;
    }

    @Transactional
    @Override
    public void editWorkExp(EditWorkExpBO workExpBO) {

        ResumeWorkExp workExp = new ResumeWorkExp();
        BeanUtils.copyProperties(workExpBO, workExp);

        workExp.setUpdatedTime(LocalDateTime.now());

        if (StrUtil.isBlank(workExp.getId())) {
            // id为空则新增
            workExp.setCreateTime(LocalDateTime.now());
            workExpMapper.insert(workExp);
        } else {
            // id不为空则修改
            workExpMapper.update(workExp,
                    new QueryWrapper<ResumeWorkExp>()
                            .eq("id", workExpBO.getId())
                            .eq("user_id", workExpBO.getUserId())
                            .eq("resume_id", workExpBO.getResumeId())
            );
        }

        redis.del(REDIS_RESUME_INFO + ":" + workExpBO.getUserId());
    }

    @Override
    public ResumeWorkExp getWorkExp(String workExpId, String userId) {

        ResumeWorkExp workExp = workExpMapper.selectOne(
                new QueryWrapper<ResumeWorkExp>()
                        .eq("id", workExpId)
                        .eq("user_id", userId)
        );
        return workExp;
    }

    @Transactional
    @Override
    public void deleteWorkExp(String workExpId, String userId) {
        workExpMapper.delete(
                new QueryWrapper<ResumeWorkExp>()
                        .eq("id", workExpId)
                        .eq("user_id", userId)
        );

        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    @Transactional
    @Override
    public void editProjectExp(EditProjectExpBO projectExpBO) {

        ResumeProjectExp projectExp = new ResumeProjectExp();
        BeanUtils.copyProperties(projectExpBO, projectExp);

        projectExp.setUpdatedTime(LocalDateTime.now());

        if (StrUtil.isBlank(projectExpBO.getId())) {
            // 新增
            projectExp.setCreateTime(LocalDateTime.now());
            projectExpMapper.insert(projectExp);
        } else {
            // 修改
            projectExpMapper.update(projectExp,
                    new QueryWrapper<ResumeProjectExp>()
                            .eq("id", projectExpBO.getId())
                            .eq("user_id", projectExpBO.getUserId())
                            .eq("resume_id", projectExpBO.getResumeId())
            );
        }

        redis.del(REDIS_RESUME_INFO + ":" + projectExpBO.getUserId());
    }


    @Override
    public ResumeProjectExp getProjectExp(String projectExpId, String userId) {

        ResumeProjectExp projectExp = projectExpMapper.selectOne(
                new QueryWrapper<ResumeProjectExp>()
                        .eq("id", projectExpId)
                        .eq("user_id", userId)
        );
        return projectExp;
    }

    @Transactional
    @Override
    public void deleteProjectExp(String projectExpId, String userId) {
        projectExpMapper.delete(
                new QueryWrapper<ResumeProjectExp>()
                        .eq("id", projectExpId)
                        .eq("user_id", userId)
        );

        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }


    @Transactional
    @Override
    public void editEducation(EditEducationBO educationBO) {

        ResumeEducation education = new ResumeEducation();
        BeanUtils.copyProperties(educationBO, education);

        education.setUpdatedTime(LocalDateTime.now());

        if (StrUtil.isBlank(educationBO.getId())) {
            // 新增
            education.setCreateTime(LocalDateTime.now());
            educationMapper.insert(education);
        } else {
            // 修改
            educationMapper.update(education,
                    new QueryWrapper<ResumeEducation>()
                            .eq("id", educationBO.getId())
                            .eq("user_id", educationBO.getUserId())
                            .eq("resume_id", educationBO.getResumeId())
            );
        }

        redis.del(REDIS_RESUME_INFO + ":" + educationBO.getUserId());

    }

    @Override
    public ResumeEducation getEducation(String eduId, String userId) {
        ResumeEducation education = educationMapper.selectOne(
                new QueryWrapper<ResumeEducation>()
                        .eq("id", eduId)
                        .eq("user_id", userId)
        );
        return education;
    }

    @Transactional
    @Override
    public void deleteEducation(String eduId, String userId) {
        educationMapper.delete(
                new QueryWrapper<ResumeEducation>()
                        .eq("id", eduId)
                        .eq("user_id", userId)
        );

        redis.del(REDIS_RESUME_INFO + ":" + userId);
    }

    @Transactional
    @Override
    public void editJobExpect(EditResumeExpectBO expectBO) {

        ResumeExpect resumeExpect = new ResumeExpect();
        BeanUtils.copyProperties(expectBO, resumeExpect);

        resumeExpect.setUpdatedTime(LocalDateTime.now());

        if (StrUtil.isBlank(expectBO.getId())) {
            // 新增
            resumeExpect.setCreateTime(LocalDateTime.now());
            expectMapper.insert(resumeExpect);
        } else {
            // 修改
            expectMapper.update(resumeExpect,
                    new QueryWrapper<ResumeExpect>()
                            .eq("id", expectBO.getId())
                            .eq("user_id", expectBO.getUserId())
                            .eq("resume_id", expectBO.getResumeId())
            );
        }

        redis.del(REDIS_RESUME_EXPECT + ":" + expectBO.getUserId());
    }

    @Override
    public List<ResumeExpect> getMyResumeExpectList(String resumeId, String userId) {

        List<ResumeExpect> expectList = expectMapper.selectList(
                new QueryWrapper<ResumeExpect>()
                        .eq("resume_id", resumeId)
                        .eq("user_id", userId)
                        .orderByDesc("updated_time")
        );

        redis.set(REDIS_RESUME_EXPECT + ":" + userId,
                GsonUtils.object2String(expectList));

        return expectList;
    }

    @Transactional
    @Override
    public void deleteResumeExpect(String resumeExpectId, String userId) {
        expectMapper.delete(
                new QueryWrapper<ResumeExpect>()
                        .eq("id", resumeExpectId)
                        .eq("user_id", userId)
        );

        redis.del(REDIS_RESUME_EXPECT + ":" + userId);
    }

}





package com.zack.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.EditResumeBO;
import com.zack.bo.EditWorkExpBO;
import com.zack.domain.Resume;
import com.zack.domain.ResumeEducation;
import com.zack.domain.ResumeProjectExp;
import com.zack.domain.ResumeWorkExp;
import com.zack.mapper.ResumeMapper;
import com.zack.mapper.ResumeWorkExpMapper;
import com.zack.service.ResumeService;
import com.zack.vo.ResumeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

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
        // List<ResumeWorkExp> workExpList = WorkExpMapper.selectList(
        //         new QueryWrapper<ResumeWorkExp>()
        //                 .eq("user_id", userId)
        //                 .eq("resume_id", resume.getId())
        //                 .orderByDesc("begin_date")
        // );
        //
        // // 3. 查询项目经验
        // List<ResumeProjectExp> projectExpList = projectExpMapper.selectList(
        //         new QueryWrapper<ResumeProjectExp>()
        //                 .eq("user_id", userId)
        //                 .eq("resume_id", resume.getId())
        //                 .orderByDesc("begin_date")
        // );
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
        // resumeVO.setWorkExpList(workExpList);
        // resumeVO.setProjectExpList(projectExpList);
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
}





package com.zack.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.domain.Resume;
import com.zack.mapper.ResumeMapper;
import com.zack.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author chenzhiqiang
* @description 针对表【resume(简历表)】的数据库操作Service实现
* @createDate 2025-05-10 22:52:49
*/
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume>
    implements ResumeService{

    @Autowired
    private ResumeMapper resumeMapper;
    @Override
    public void initResume(String userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        resumeMapper.insert(resume);
    }
}





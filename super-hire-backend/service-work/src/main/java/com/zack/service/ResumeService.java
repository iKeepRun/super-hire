package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.domain.Resume;

/**
* @author chenzhiqiang
* @description 针对表【resume(简历表)】的数据库操作Service
* @createDate 2025-05-10 22:52:49
*/
public interface ResumeService extends IService<Resume> {

    void initResume(String userId);
}

package com.zack.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.bo.EditResumeBO;
import com.zack.bo.EditWorkExpBO;
import com.zack.domain.Resume;
import com.zack.vo.ResumeVO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
* @author chenzhiqiang
* @description 针对表【resume(简历表)】的数据库操作Service
* @createDate 2025-05-10 22:52:49
*/
public interface ResumeService  {

    void initResume(String userId);

    public void modifyResume(EditResumeBO editResumeBO) ;


    public ResumeVO getResumeInfo(String userId) ;
    public void editWorkExp(EditWorkExpBO workExpBO);
}

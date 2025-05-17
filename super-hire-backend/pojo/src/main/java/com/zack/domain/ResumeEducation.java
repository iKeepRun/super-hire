package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 教育经历表
 * @TableName resume_education
 */
@TableName(value ="resume_education")
@Data
public class ResumeEducation {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的教育经历，用户id
     */
    private String userId;

    /**
     * 属于哪份简历id
     */
    private String resumeId;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 学历
     */
    private String education;

    /**
     * 专业名称
     */
    private String major;

    /**
     * 开始日期
     */
    private String beginDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updatedTime;
}
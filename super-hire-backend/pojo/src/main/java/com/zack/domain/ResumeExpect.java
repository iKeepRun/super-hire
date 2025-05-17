package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 求职期望表
 * @TableName resume_expect
 */
@TableName(value ="resume_expect")
@Data
public class ResumeExpect {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的求职期望，用户id
     */
    private String userId;

    /**
     * 属于哪份简历id
     */
    private String resumeId;

    /**
     * 期望职位
     */
    private String jobName;

    /**
     * 工作所在城市
     */
    private String city;

    /**
     * 工作对应所处行业
     */
    private String industry;

    /**
     * 薪资要求区间-起始
     */
    private Integer beginSalary;

    /**
     * 薪资要求区间-结束
     */
    private Integer endSalary;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updatedTime;
}
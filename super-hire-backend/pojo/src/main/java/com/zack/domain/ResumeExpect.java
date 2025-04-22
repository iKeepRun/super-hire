package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 求职期望表
 * @TableName resume_expect
 */
@TableName(value ="resume_expect")
@Data
public class ResumeExpect implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的求职期望，用户id
     */
    private String user_id;

    /**
     * 属于哪份简历id
     */
    private String resume_id;

    /**
     * 期望职位
     */
    private String job_name;

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
    private Integer begin_salary;

    /**
     * 薪资要求区间-结束
     */
    private Integer end_salary;

    /**
     * 
     */
    private Date create_time;

    /**
     * 
     */
    private Date updated_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
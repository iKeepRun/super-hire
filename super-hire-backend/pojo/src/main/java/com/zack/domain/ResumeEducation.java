package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 教育经历表
 * @TableName resume_education
 */
@TableName(value ="resume_education")
@Data
public class ResumeEducation implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的教育经历，用户id
     */
    private String user_id;

    /**
     * 属于哪份简历id
     */
    private String resume_id;

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
    private String begin_date;

    /**
     * 结束日期
     */
    private String end_date;

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
package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工作经验表
 * @TableName resume_work_exp
 */
@TableName(value ="resume_work_exp")
@Data
public class ResumeWorkExp implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的工作经验，用户id
     */
    private String user_id;

    /**
     * 属于哪份简历id
     */
    private String resume_id;

    /**
     * 就职公司名称
     */
    private String company_name;

    /**
     * 行业
     */
    private String industry;

    /**
     * 在职时间-开始
     */
    private String begin_date;

    /**
     * 在职时间-结束
     */
    private String end_date;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 所在部门名称
     */
    private String department;

    /**
     * 工作内容、业绩、职责等
     */
    private String content;

    /**
     * 工作内容、业绩、职责等
     */
    private String content_html;

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
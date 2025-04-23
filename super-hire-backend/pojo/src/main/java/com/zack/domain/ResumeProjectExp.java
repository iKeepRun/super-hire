package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 项目经验表
 * @TableName resume_project_exp
 */
@TableName(value ="resume_project_exp")
@Data
public class ResumeProjectExp implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的项目经验，用户id
     */
    private String user_id;

    /**
     * 属于哪份简历id
     */
    private String resume_id;

    /**
     * 项目名称
     */
    private String project_name;

    /**
     * 担任角色
     */
    private String role_name;

    /**
     * 项目开始日期
     */
    private String begin_date;

    /**
     * 项目结束日期
     */
    private String end_date;

    /**
     * 项目描述
     */
    private String content;

    /**
     * 项目描述
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
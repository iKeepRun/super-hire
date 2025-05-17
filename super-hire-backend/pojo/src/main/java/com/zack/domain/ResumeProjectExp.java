package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 项目经验表
 * @TableName resume_project_exp
 */
@TableName(value ="resume_project_exp")
@Data
public class ResumeProjectExp {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的项目经验，用户id
     */
    private String userId;

    /**
     * 属于哪份简历id
     */
    private String resumeId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 担任角色
     */
    private String roleName;

    /**
     * 项目开始日期
     */
    private String beginDate;

    /**
     * 项目结束日期
     */
    private String endDate;

    /**
     * 项目描述
     */
    private String content;

    /**
     * 项目描述
     */
    private String contentHtml;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updatedTime;
}
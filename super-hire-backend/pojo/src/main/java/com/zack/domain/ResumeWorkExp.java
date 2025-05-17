package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 工作经验表
 * @TableName resume_work_exp
 */
@TableName(value ="resume_work_exp")
@Data
public class ResumeWorkExp {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的工作经验，用户id
     */
    private String userId;

    /**
     * 属于哪份简历id
     */
    private String resumeId;

    /**
     * 就职公司名称
     */
    private String companyName;

    /**
     * 行业
     */
    private String industry;

    /**
     * 在职时间-开始
     */
    private String beginDate;

    /**
     * 在职时间-结束
     */
    private String endDate;

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
package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 举报职位
 * @TableName report_job
 */
@TableName(value ="report_job")
@Data
public class ReportJob {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 被举报的职位id
     */
    private String jobId;

    /**
     * 举报人id
     */
    private String reportUserId;

    /**
     * 举报人姓名
     */
    private String reportUserName;

    /**
     * 举报原因
     */
    private String reportReason;

    /**
     * 被举报的职位名称
     */
    private String jobName;

    /**
     * 被举报的公司名称
     */
    private String companyName;

    /**
     * 处理状态：0：待处理，1：已处理，2：忽略、无需处理
     */
    private Integer dealStatus;

    /**
     * 
     */
    private LocalDateTime createdTime;

    /**
     * 
     */
    private LocalDateTime updatedTime;
}
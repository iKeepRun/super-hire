package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 举报职位
 * @TableName report_job
 */
@TableName(value ="report_job")
@Data
public class ReportJob implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 被举报的职位id
     */
    private String job_id;

    /**
     * 举报人id
     */
    private String report_user_id;

    /**
     * 举报人姓名
     */
    private String report_user_name;

    /**
     * 举报原因
     */
    private String report_reason;

    /**
     * 被举报的职位名称
     */
    private String job_name;

    /**
     * 被举报的公司名称
     */
    private String company_name;

    /**
     * 处理状态：0：待处理，1：已处理，2：忽略、无需处理
     */
    private Integer deal_status;

    /**
     * 
     */
    private Date created_time;

    /**
     * 
     */
    private Date updated_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * HR发布的职位表
 * @TableName job
 */
@TableName(value ="job")
@Data
public class Job implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的职位，HR角色用户id
     */
    private String hr_id;

    /**
     * 职位所属哪家公司的，HR离职后则不能查询到
     */
    private String company_id;

    /**
     * 职位名称
     */
    private String job_name;

    /**
     * 职位类别
     */
    private String job_type;

    /**
     * 工作经验年限
     */
    private String exp_years;

    /**
     * 技能标签
     */
    private String edu;

    /**
     * 薪资要求区间-起始
     */
    private Integer begin_salary;

    /**
     * 薪资要求区间-结束
     */
    private Integer end_salary;

    /**
     * 总共几个月工资
     */
    private Integer monthly_salary;

    /**
     * 职位描述
     */
    private String job_desc;

    /**
     * 职位标签
     */
    private String tags;

    /**
     * 工作城市
     */
    private String city;

    /**
     * 工作地点
     */
    private String address;

    /**
     * 1：招聘中，open
2：已关闭，close
3：违规删除，delete
     */
    private Integer status;

    /**
     * 违规原因
     */
    private String violate_reason;

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
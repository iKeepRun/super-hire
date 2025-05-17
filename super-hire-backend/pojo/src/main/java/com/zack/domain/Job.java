package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * HR发布的职位表
 * @TableName job
 */
@TableName(value ="job")
@Data
public class Job {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的职位，HR角色用户id
     */
    private String hrId;

    /**
     * 职位所属哪家公司的，HR离职后则不能查询到
     */
    private String companyId;

    /**
     * 职位名称
     */
    private String jobName;

    /**
     * 职位类别
     */
    private String jobType;

    /**
     * 工作经验年限
     */
    private String expYears;

    /**
     * 技能标签
     */
    private String edu;

    /**
     * 薪资要求区间-起始
     */
    private Integer beginSalary;

    /**
     * 薪资要求区间-结束
     */
    private Integer endSalary;

    /**
     * 总共几个月工资
     */
    private Integer monthlySalary;

    /**
     * 职位描述
     */
    private String jobDesc;

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
    private String violateReason;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updatedTime;
}
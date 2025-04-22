package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 面试邀约表
本表为次表，可做冗余，可以用mongo或者es替代
 * @TableName interview
 */
@TableName(value ="interview")
@Data
public class Interview implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 本面试属于哪个hr的
     */
    private String hr_user_id;

    /**
     * 本面试属于哪一个公司的
     */
    private String company_id;

    /**
     * 面试者，候选人id
     */
    private String cand_user_id;

    /**
     * 面试的岗位id
     */
    private String job_id;

    /**
     * 面试的岗位名称
     */
    private String job_name;

    /**
     * 面试时间
     */
    private Date interview_time;

    /**
     * 面试地点
     */
    private String interview_address;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 面试邀约的状态
1：等待候选人接受面试
2：候选人已接受面试
3：候选人已拒绝面试
4：HR已取消面试
5：面试通过
     */
    private Integer status;

    /**
     * 候选人名称（候选人名称）  
简历名称与职位使用字段冗余，目的相当于快照，只记录当时信息
     */
    private String cand_name;

    /**
     * 候选人头像  
简历名称与职位使用字段冗余，目的相当于快照，只记录当时信息
     */
    private String cand_face;

    /**
     * 候选人职位  
简历名称与职位使用字段冗余，目的相当于快照，只记录当时信息
     */
    private String cand_position;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
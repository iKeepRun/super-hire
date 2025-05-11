package com.zack.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 简历表
 * @TableName resume
 */
@TableName(value ="resume")
@Data
public class Resume {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 谁的简历，用户id
     */
    private String userId;

    /**
     * 个人优势
     */
    private String advantage;

    /**
     * 个人优势的html内容，用于展示
     */
    private String advantageHtml;

    /**
     * 资格证书
     */
    private String credentials;

    /**
     * 技能标签
     */
    private String skills;

    /**
     * 求职状态
     */
    private String status;

    /**
     * 刷新简历时间
     */
    private Date refreshTime;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updatedTime;
}
package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章表
 * @TableName article
 */
@TableName(value ="article")
@Data
public class Article implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容，长度不超过9999，自行在前后端判断
     */
    private String content;

    /**
     * 文章封面图，article_type=1 的时候展示
     */
    private String article_cover;

    /**
     * 发布者amin_id
     */
    private String publish_admin_id;

    /**
     * 文章发布时间（也是预约发布的时间）
     */
    private Date publish_time;

    /**
     * 发布者，字段冗余，避免多表关联
     */
    private String publisher;

    /**
     * 发布者头像，字段冗余
     */
    private String publisher_face;

    /**
     * 文章状态：0：关闭，待发布，1：正常，可查阅，2：删除，无法查看
     */
    private Integer status;

    /**
     * 
     */
    private Date create_time;

    /**
     * 
     */
    private Date update_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
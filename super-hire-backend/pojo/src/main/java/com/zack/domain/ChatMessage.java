package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天信息存储表
 * @TableName chat_message
 */
@TableName(value ="chat_message")
@Data
public class ChatMessage implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 发送者的用户id
     */
    private String sender_id;

    /**
     * 接受者的用户id
     */
    private String receiver_id;

    /**
     * 消息接受者的类型，是HR还是求职者，目的为了让前端不同页面接受判断并且处理显示
     */
    private Integer receiver_type;

    /**
     * 聊天内容
     */
    private String msg;

    /**
     * 消息类型，有文字类、图片类、视频类...等，详见枚举类
     */
    private Integer msg_type;

    /**
     * 消息的聊天时间，既是发送者的发送时间、又是接受者的接受时间
     */
    private Date chat_time;

    /**
     * 标记存储数据库，用于历史展示。每超过1分钟，则显示聊天时间，前端可以控制时间长短
     */
    private Integer show_msg_date_time_flag;

    /**
     * 视频地址
     */
    private String video_path;

    /**
     * 视频宽度
     */
    private Integer video_width;

    /**
     * 视频高度
     */
    private Integer video_height;

    /**
     * 视频时间
     */
    private Integer video_times;

    /**
     * 语音地址
     */
    private String voice_path;

    /**
     * 语音时长
     */
    private Integer speak_voice_duration;

    /**
     * 语音消息标记是否已读未读，true: 已读，false: 未读
     */
    private Integer is_read;

    /**
     * 候选人用户id
     */
    private String resume_user_id;

    /**
     * 简历名称（候选人名称）  
简历名称与职位使用字段冗余，目的相当于快照，只记录当时信息
     */
    private String resume_name;

    /**
     * 候选人职位  
简历名称与职位使用字段冗余，目的相当于快照，只记录当时信息
     */
    private String resume_position;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
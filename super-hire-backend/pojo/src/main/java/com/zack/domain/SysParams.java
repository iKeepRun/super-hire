package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统参数配置表，本表仅有一条记录
 * @TableName sys_params
 */
@TableName(value ="sys_params")
@Data
public class SysParams {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 每日最大简历被刷新的次数
     */
    private Integer maxResumeRefreshCounts;
}
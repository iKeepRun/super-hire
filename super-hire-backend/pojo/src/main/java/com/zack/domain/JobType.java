package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 职位类别
 * @TableName job_type
 */
@TableName(value ="job_type")
@Data
public class JobType {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 行业名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 为0则当前为第一级分类，否则当前为fatherId所对应行业的子分类
     */
    private String fatherId;

    /**
     * 用于提供给前端tree显示标记是否展示下拉箭头，一级分类与二级分类为true，三级分类也就是最后一级分类是false
     */
    private Integer isLeaf;

    /**
     * 分类层级，用于标记
     */
    private Integer level;
}
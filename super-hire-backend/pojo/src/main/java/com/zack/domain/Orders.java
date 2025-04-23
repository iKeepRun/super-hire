package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 * @TableName orders
 */
@TableName(value ="orders")
@Data
public class Orders implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 用户主键id
     */
    private String user_id;

    /**
     * 购买用户所在的企业主键id
     */
    private String company_id;

    /**
     * 商品名称
     */
    private String item_name;

    /**
     * 订单总价
     */
    private Integer total_amount;

    /**
     * 实际支付总价格
     */
    private Integer real_pay_amount;

    /**
     * 邮费;默认可以为零，代表包邮
     */
    private Integer post_amount;

    /**
     * 支付方式
     */
    private Integer pay_method;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 创建时间
     */
    private Date created_time;

    /**
     * 更新时间
     */
    private Date updated_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
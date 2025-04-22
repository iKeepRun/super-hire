package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 企业相册表，本表只存企业上传的图片
 * @TableName company_photo
 */
@TableName(value ="company_photo")
@Data
public class CompanyPhoto implements Serializable {
    /**
     * 企业id
     */
    @TableId
    private String company_id;

    /**
     * 
     */
    private String photos;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
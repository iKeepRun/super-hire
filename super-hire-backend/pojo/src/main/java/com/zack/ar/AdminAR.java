package com.zack.ar;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册
 * </p>
 *
 * @author 风间影月
 * @since 2022-10-08
 */
@TableName("admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAR extends Model<AdminAR> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 登录名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户混合加密的盐
     */
    private String slat;

    /**
     * 备注
     */
    private String remark;

    /**
     * 头像
     */
    private String face;

    private Date create_time;

    private Date updated_time;



    @Override
    public Serializable pkVal() {
        return this.id;
    }


}

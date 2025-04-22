package com.zack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 企业表
 * @TableName company
 */
@TableName(value ="company")
@Data
public class Company implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 企业名称
     */
    private String company_name;

    /**
     * 企业短名
     */
    private String short_name;

    /**
     * 企业logo
     */
    private String logo;

    /**
     * 营业执照
     */
    private String biz_license;

    /**
     * 企业所在国家
     */
    private String country;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在市
     */
    private String city;

    /**
     * 所在区县
     */
    private String district;

    /**
     * 公司办公地址
     */
    private String address;

    /**
     * 公司性质
     */
    private String nature;

    /**
     * 人员规模/企业规模
     */
    private String people_size;

    /**
     * 所在行业
     */
    private String industry;

    /**
     * 融资阶段
     */
    private String financ_stage;

    /**
     * 工作时间，例：9:00-18:00 周末单休
     */
    private String work_time;

    /**
     * 公司简介
     */
    private String introduction;

    /**
     * 公司优势，例：领导和睦 岗位晋升

     */
    private String advantage;

    /**
     * 福利待遇，例：五险一金 定期体检

     */
    private String benefits;

    /**
     * 薪资福利（奖金），例：年底双薪
     */
    private String bonus;

    /**
     * 补助津贴，例：住房补贴
     */
    private String subsidy;

    /**
     * 成立时间
     */
    private Date build_date;

    /**
     * 注册资本
     */
    private String regist_capital;

    /**
     * 注册地址
     */
    private String regist_place;

    /**
     * 法人代表
     */
    private String legal_representative;

    /**
     * 审核状态
0：未发起审核认证(未进入审核流程)
1：审核认证通过
2：审核认证失败
3：审核中（等待审核）
     */
    private Integer review_status;

    /**
     * 审核回复/审核意见
     */
    private String review_replay;

    /**
     * 入驻平台授权书
     */
    private String auth_letter;

    /**
     * 提交申请人的用户id
     */
    private String commit_user_id;

    /**
     * 提交申请人的手机号
     */
    private String commit_user_mobile;

    /**
     * 提交审核的日期
     */
    private Date commit_date;

    /**
     * 0: 否  1: 是
     */
    private Integer is_vip;

    /**
     * Vip过期日期，判断企业是否vip，需要is_vip=1并且过期日期>=当前日期
     */
    private Date vip_expire_date;

    /**
     * 
     */
    private Date created_time;

    /**
     * 
     */
    private Date updated_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
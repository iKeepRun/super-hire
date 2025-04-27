package com.zack.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PARAMS_ERROR(40000,"请求参数异常"),
    NO_AUTH_ERROR(40101,"无权限"),
    NOT_FOUND_ERROR(40102,"请求数据不存在"),
    FRORBIDDEN_ERROR(40300,"禁止访问"),
    SYSTEM_ERROR(50000,"系统内部异常"),
    OPERATION_ERROR(50001,"操作失败"),

    // 50x
    UN_LOGIN(501,"请登录后再继续操作！"),
    TICKET_INVALID(502,"会话失效，请重新登录！"),
    HR_TICKET_INVALID(5021,"手机端会话失效，请重新登录！"),
    NO_AUTH(503,"您的权限不足，无法继续操作！"),
    MOBILE_ERROR(504,"短信发送失败，请稍后重试！"),
    SMS_NEED_WAIT_ERROR(505,"短信发送太快啦~请稍后再试！"),
    SMS_CODE_ERROR(506,"验证码过期或不匹配，请稍后再试！"),
    USER_FROZEN(507,"用户已被冻结，请联系管理员！"),
    USER_UPDATE_ERROR(508,"用户信息更新失败，请联系管理员！"),
    USER_INACTIVE_ERROR(509,"请前往[账号设置]修改信息激活后再进行后续操作！"),
    USER_INFO_UPDATED_ERROR(5091,"用户信息修改失败！"),
    USER_INFO_UPDATED_NICKNAME_EXIST_ERROR(5092,"昵称已经存在！"),
    USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR(5092,"慕课号已经存在！"),
    USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR(5092,"慕课号无法修改！"),
    FILE_UPLOAD_NULL_ERROR(510,"文件不能为空，请选择一个文件再上传！"),
    FILE_UPLOAD_FAILD(511,"文件上传失败！"),
    FILE_FORMATTER_FAILD(512,"文件图片格式不支持！"),
    FILE_MAX_SIZE_500KB_ERROR(5131,"仅支持500kb大小以下的文件上传！"),
    FILE_MAX_SIZE_2MB_ERROR(5132,"仅支持2MB大小以下的文件上传！"),
    FILE_MAX_SIZE_8MB_ERROR(5132,"体验版仅支持8MB以下的文件上传！"),
    FILE_MAX_SIZE_100MB_ERROR(5132,"仅支持100MB大小以下的文件上传！"),
    FILE_NOT_EXIST_ERROR(514,"你所查看的文件不存在！"),
    USER_STATUS_ERROR(515,"用户状态参数出错！"),
    USER_NOT_EXIST_ERROR(516,"用户不存在！"),
    USER_PARAMS_ERROR(517,"用户请求参数出错！"),

    // 自定义系统级别异常 54x
    SYSTEM_INDEX_OUT_OF_BOUNDS(541,  "系统错误，数组越界！"),
    SYSTEM_ARITHMETIC_BY_ZERO(542,  "系统错误，无法除零！"),
    SYSTEM_NULL_POINTER(543,  "系统错误，空指针！"),
    SYSTEM_NUMBER_FORMAT(544,  "系统错误，数字转换异常！"),
    SYSTEM_PARSE(545,  "系统错误，解析异常！"),
    SYSTEM_IO(546,  "系统错误，IO输入输出异常！"),
    SYSTEM_FILE_NOT_FOUND(547,  "系统错误，文件未找到！"),
    SYSTEM_CLASS_CAST(548,  "系统错误，类型强制转换错误！"),
    SYSTEM_PARSER_ERROR(549,  "系统错误，解析出错！"),
    SYSTEM_DATE_PARSER_ERROR(550,  "系统错误，日期解析出错！"),
    SYSTEM_NO_EXPIRE_ERROR(552,  "系统错误，缺少过期时间！"),

    HTTP_URL_CONNECT_ERROR(551,  "目标地址无法请求！"),

    // admin 管理系统 56x
    ADMIN_USERNAME_NULL_ERROR(561,  "管理员登录名不能为空！"),
    ADMIN_USERNAME_EXIST_ERROR(562,  "管理员账户名已存在！"),
    ADMIN_NAME_NULL_ERROR(563,  "管理员负责人不能为空！"),
    ADMIN_PASSWORD_ERROR(564,  "密码不能为空或者两次输入不一致！"),
    ADMIN_CREATE_ERROR(565,  "添加管理员失败！"),
    ADMIN_PASSWORD_NULL_ERROR(566,  "密码不能为空！"),
    ADMIN_LOGIN_ERROR(567,  "管理员不存在或密码不正确！"),
    ADMIN_FACE_NULL_ERROR(568,  "人脸信息不能为空！"),
    ADMIN_FACE_LOGIN_ERROR(569,  "人脸识别失败，请重试！"),
    ADMIN_DELETE_ERROR(5691,  "删除管理员失败！"),
    CATEGORY_EXIST_ERROR(570,  "文章分类已存在，请换一个分类名！"),

    // 媒体中心 相关错误 58x
    ARTICLE_COVER_NOT_EXIST_ERROR(580,  "文章封面不存在，请选择一个！"),
    ARTICLE_CATEGORY_NOT_EXIST_ERROR(581,  "请选择正确的文章领域！"),
    ARTICLE_CREATE_ERROR(582,  "创建文章失败，请重试或联系管理员！"),
    ARTICLE_QUERY_PARAMS_ERROR(583,  "文章列表查询参数错误！"),
    ARTICLE_DELETE_ERROR(584,  "文章删除失败！"),
    ARTICLE_WITHDRAW_ERROR(585,  "文章撤回失败！"),
    ARTICLE_REVIEW_ERROR(585,  "文章审核出错！"),
    ARTICLE_ALREADY_READ_ERROR(586,  "文章重复阅读！"),

    COMPANY_INFO_UPDATED_ERROR(5151,"企业信息修改失败！"),
    COMPANY_INFO_UPDATED_NO_AUTH_ERROR(5151,"当前用户无权修改企业信息！"),
    COMPANY_IS_NOT_VIP_ERROR(5152,"企业非VIP或VIP特权已过期，请至企业后台充值续费！"),


    // 人脸识别错误代码
    FACE_VERIFY_TYPE_ERROR(600,  "人脸比对验证类型不正确！"),
    FACE_VERIFY_LOGIN_ERROR(601,  "人脸登录失败！"),

    // 系统错误，未预期的错误 555
    SYSTEM_OPERATION_ERROR(556,  "操作失败，请重试或联系管理员"),
    SYSTEM_RESPONSE_NO_INFO(557,  ""),
    SYSTEM_ERROR_GLOBAL(558,  "全局降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_FEIGN(559,  "客户端Feign降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_ZUUL(560,  "请求系统过于繁忙，请稍后再试！"),
    SYSTEM_PARAMS_SETTINGS_ERROR(5611,  "参数设置不规范！"),
    ZOOKEEPER_BAD_VERSION_ERROR(5612,  "数据过时，请刷新页面重试！"),

    DATA_DICT_EXIST_ERROR(5631,  "数据字典已存在，不可重复添加或修改！"),
    DATA_DICT_DELETE_ERROR(5632,  "删除数据字典失败！"),

    REPORT_RECORD_EXIST_ERROR(5721,  "请不要重复举报噢~！"),

    RESUME_MAX_LIMIT_ERROR(5711,  "本日简历刷新次数已达上限！"),

    JWT_SIGNATURE_ERROR(5555,  "用户校验失败，请重新登录！"),
    JWT_EXPIRE_ERROR(5556,  "登录有效期已过，请重新登录！"),

    // 支付错误相关代码
    PAYMENT_USER_INFO_ERROR(5901,  "用户id或密码不正确！"),
    PAYMENT_ACCOUT_EXPIRE_ERROR(5902,  "该账户授权访问日期已失效！"),
    PAYMENT_HEADERS_ERROR(5903,  "请在header中携带支付中心所需的用户id以及密码！"),
    PAYMENT_ORDER_CREATE_ERROR(5904,  "支付中心订单创建失败，请联系管理员！"),

    // admin 相关错误代码
    ADMIN_NOT_EXIST(5101,  "管理员不存在！");
    

    private final Integer code;
    private final String msg;

    ErrorCode(Integer code,String  msg){
        this.code=code;
        this.msg=msg;
    }

}

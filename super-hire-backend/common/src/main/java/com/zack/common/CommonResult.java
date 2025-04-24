package com.zack.common;

import com.zack.exceptions.ErrorCode;

public class CommonResult<T> {
    private int code;
    private String msg;
    private boolean success;
    private T data;


    public CommonResult(int code, String msg, Boolean success,T data) {
        this.code = code;
        this.msg = msg;
        this.success=success;
        this.data = data;
    }

    public static <T> CommonResult<T> success(){
        return new CommonResult(0,"操作成功",true,null);
    }

    public static <T> CommonResult<T> success(T data){
        return new CommonResult(0,"操作成功",true,data);
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode){
        return new CommonResult(errorCode.getCode(),errorCode.getMsg(),false,null);
    }
    public static <T> CommonResult<T> error(String message){
        return new CommonResult(50001,message,false,null);
    }

    public static <T> CommonResult<T> error(int code,String message){
        return new CommonResult(code,message,false,null);
    }
}

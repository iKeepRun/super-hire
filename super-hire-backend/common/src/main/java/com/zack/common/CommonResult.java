package com.zack.common;

import com.zack.exceptions.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CommonResult<T> implements Serializable {
    private int status;
    private String msg;
    private boolean success;
    private T data;


    public CommonResult(int status, String msg, Boolean success,T data) {
        this.status = status;
        this.msg = msg;
        this.success=success;
        this.data = data;
    }

    public static <T> CommonResult<T> success(){
        return new CommonResult(200,"操作成功",true,null);
    }

    public static <T> CommonResult<T> success(T data){
        return new CommonResult(200,"操作成功",true,data);
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


    public static <T> CommonResult<T> error(Map<String,String> map){
        return new CommonResult(50001,"操作失败",false,map);
    }
}

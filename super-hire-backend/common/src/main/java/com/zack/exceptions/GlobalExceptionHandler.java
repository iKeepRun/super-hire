package com.zack.exceptions;

import com.zack.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException",e);
        return CommonResult.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> runTimeExceptionHandler(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException",e);

        BindingResult result = e.getBindingResult();
        Map<String, String> errors = getErrors(result);
        return CommonResult.error(errors);
    }



    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> runTimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return CommonResult.error(ErrorCode.SYSTEM_ERROR);
    }



    public Map<String, String> getErrors(BindingResult result) {

        Map<String, String> map = new HashMap<>();

        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError fe : errorList) {
            // 错误所对应的属性字段名
            String field = fe.getField();
            // 错误信息
            String message = fe.getDefaultMessage();

            map.put(field, message);
        }

        return map;
    }
}





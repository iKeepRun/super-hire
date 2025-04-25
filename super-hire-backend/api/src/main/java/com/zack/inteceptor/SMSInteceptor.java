package com.zack.inteceptor;

import com.zack.common.CommonResult;
import com.zack.exceptions.ErrorCode;
import com.zack.utils.GsonUtils;
import com.zack.utils.IPUtil;
import com.zack.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
@Slf4j
@Component
public class SMSInteceptor implements HandlerInterceptor {
    @Autowired
    private RedisOperator redisOperator;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("手机号：{}",request.getParameter("mobile"));
//        String ip = IPUtil.getRequestIp(request);
        if (redisOperator.keyIsExist(request.getParameter("mobile"))) {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            CommonResult<Object> commonResult = CommonResult.error("发送过于频繁请稍后再试");
            writer.write(GsonUtils.object2String(commonResult));
            writer.flush();
            writer.close();
            return false;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

package com.zack;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class RecordExcutorTimeAspect {
    @Around("execution(* com.zack.service..*.*(..))")
    public Object recordTime(ProceedingJoinPoint joinPoint) throws Throwable {
        //
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("task");
        Object proceed = joinPoint.proceed();
        log.info("执行类：{}", joinPoint.getTarget().getClass().getName());
        log.info("执行方法：{}", joinPoint.getSignature().toShortString());

        stopWatch.stop();
        log.info("执行时间：{}", stopWatch.getTotalTimeMillis());
        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());

        return proceed;
    }
}

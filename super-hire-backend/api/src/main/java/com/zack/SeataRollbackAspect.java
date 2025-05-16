package com.zack;

import cn.hutool.core.util.StrUtil;
import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


/**
 * @Description Seata手动回滚事务切面
 * @Author Zack
 * @Date 2020/3/22 15:51
 */
@Component
@Aspect
@Slf4j
public class SeataRollbackAspect {

    @Before("execution(* com.zack.service..*.*(..))")
    public void begintx(JoinPoint joinPoint) throws Throwable {
        log.info("Seata事务开始");
        //手动开启全局事务
        GlobalTransaction gt = GlobalTransactionContext.getCurrent();
        gt.begin();
    }

    @AfterThrowing(pointcut = "execution(* com.zack.service..*.*(..))",
            throwing = "throwable")
    public void seataRollback(Throwable throwable) throws Throwable {
        log.error("Seata事务回滚异常", throwable);
       //获取当前线程的事务id
        String xid = RootContext.getXID();
        if(StrUtil.isNotBlank(xid)){
            //手动回滚全局事务
            GlobalTransactionContext.reload(xid).rollback();
        }
    }
}

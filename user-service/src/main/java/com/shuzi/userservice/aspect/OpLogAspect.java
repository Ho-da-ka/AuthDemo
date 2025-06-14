package com.shuzi.userservice.aspect;

import com.alibaba.fastjson.JSON;
import com.shuzi.userservice.annotation.OpLog;
import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.domain.po.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面：统一收集成功/失败操作并发送到 MQ
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OpLogAspect {

    private final RabbitTemplate rabbitTemplate;

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint pjp, OpLog opLog) throws Throwable {
        OperationLog operationLog = new OperationLog();
        operationLog.setAction(opLog.value());
        operationLog.setUserId(BaseContext.getCurrentId());
        operationLog.setIp(BaseContext.getCurrentIp());

        try {
            Object result = pjp.proceed();
            // 记录入参详情
            operationLog.setDetail("操作成功：" + JSON.toJSONString(pjp.getArgs()));
            // 发送消息
            rabbitTemplate.convertAndSend("log.direct", "", operationLog);
            return result;
        } catch (Throwable ex) {
            // 记录入参详情
            operationLog.setDetail("操作失败：" + ex.getMessage() + JSON.toJSONString(pjp.getArgs()));
            rabbitTemplate.convertAndSend("log.direct", "", operationLog);
            // 继续抛出异常保持原有逻辑
            throw ex;
        }
    }
}
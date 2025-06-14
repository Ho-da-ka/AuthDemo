package com.shuzi.loggingservice.listener;


import com.shuzi.loggingservice.entity.OperationLog;
import com.shuzi.loggingservice.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogListener {

    private final OperationLogMapper operationLogMapper;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "log.queue"),
            exchange = @Exchange(name = "log.direct", delayed = "true")
    ))
    public void listenOperationLog(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
        log.info("保存操作日志成功：{}", operationLog);
    }
}
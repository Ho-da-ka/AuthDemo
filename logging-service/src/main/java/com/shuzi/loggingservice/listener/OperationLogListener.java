package com.shuzi.loggingservice.listener;


import com.shuzi.loggingservice.domain.po.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationLogListener {



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = ""),
            exchange = @Exchange(name = "", delayed = "true"),
            key =" MQConstants.DELAY_ORDER_KEY"
    ))
    public void listenOperationLog(OperationLog operationLog) {

    }
}
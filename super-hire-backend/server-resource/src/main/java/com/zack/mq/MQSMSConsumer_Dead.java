package com.zack.mq;

import com.rabbitmq.client.Channel;
import com.zack.utils.GsonUtils;
import com.zack.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQSMSConsumer_Dead {
    @RabbitListener(queues = {MQDeadConfig.DEAD_SMS_QUEUE})
    public void receive(Message message, Channel channel) throws Exception {
        log.info("==============================================");
            //获取routerkey
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("死信队列收到消息：{}", routingKey);
            //手动消费消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.info("==============================================");
    }
}

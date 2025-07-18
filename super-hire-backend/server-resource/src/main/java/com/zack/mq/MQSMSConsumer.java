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
public class MQSMSConsumer {
    @Autowired
    private SMSUtils smsUtils;

    @RabbitListener(queues = {MQSMSConfig.SMS_QUEUE})
    public void receive(String payload, Message message, Channel channel) throws Exception {
        try {
            //获取routerkey
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();

            if (routingKey.equals(MQSMSConfig.SMS_ROUTING_KEY)) {
                log.info("收到消息：" + payload);
                SMSContentQO smsContentQO = GsonUtils.stringToBean(payload, SMSContentQO.class);

                //TODO 发送短信(空方法，自实现)
                smsUtils.sendSMS(smsContentQO.getMobile(), smsContentQO.getContent());
            }

            //手动消费消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //手动拒绝消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }

    }
}

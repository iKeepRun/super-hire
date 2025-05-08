package com.zack.mq;

import com.google.gson.JsonObject;
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


    @RabbitListener(queues = {MQConfig.QUEUE_NAME})
    public void receive(String payload, Message message) throws Exception {
        //获取routerkey
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (routingKey.equals(MQConfig.ROUTING_KEY)) {
            log.info("收到消息：" + payload);
            SMSContentQO smsContentQO = GsonUtils.stringToBean(payload, SMSContentQO.class);

            //TODO 发送短信(空方法，自实现)
            smsUtils.sendSMS(smsContentQO.getMobile(), smsContentQO.getContent());
        }
    }
}

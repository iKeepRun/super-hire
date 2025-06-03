package com.zack.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQSMSConfig {

       public static final String SMS_EXCHANGE = "sms-exchange";
       public static final String SMS_QUEUE = "sms-queue";
       public static final String SMS_ROUTING_KEY = "sms.login";
       @Bean(SMS_EXCHANGE)
       public Exchange exchange(){
              return ExchangeBuilder.topicExchange(SMS_EXCHANGE).durable(true).build();
       }

       @Bean(SMS_QUEUE)
       public Queue queue(){
              return QueueBuilder.durable(SMS_QUEUE)
                      .withArgument("x-max-length", 5) // 队列最大长度
                      .withArgument("x-dead-letter-exchange",MQDeadConfig.DEAD_SMS_EXCHANGE)  // 绑定死信交换机
                        .withArgument("x-dead-letter-routing-key",MQDeadConfig.DEAD_ROUTING_KEY) // 绑定死信路由键
                      // .withArgument("x-message-ttl", 10000) // 消息过期时间
                      .build();
       }


       @Bean
       public Binding binding(@Qualifier(SMS_QUEUE) Queue queue,@Qualifier(SMS_EXCHANGE) Exchange exchange){
           return   BindingBuilder.bind(queue).to(exchange).with("sms.#").noargs();
       }

}

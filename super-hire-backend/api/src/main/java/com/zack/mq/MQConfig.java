package com.zack.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

       public static final String EXCHANGE_NAME = "super-sms-exchange";
       public static final String QUEUE_NAME = "super-sms-queue";
       public static final String ROUTING_KEY = "super.sms.login";
       @Bean(EXCHANGE_NAME)
       public Exchange exchange(){
              return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
       }

       @Bean(QUEUE_NAME)
       public Queue queue(){
              return QueueBuilder.durable(QUEUE_NAME)
                      .withArgument("x-message-ttl", 10000) // 消息过期时间
                      .build();
       }


       @Bean
       public Binding binding(@Qualifier(QUEUE_NAME) Queue queue,@Qualifier(EXCHANGE_NAME) Exchange exchange){
           return   BindingBuilder.bind(queue).to(exchange).with("super.sms.#").noargs();
       }

}

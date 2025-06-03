package com.zack.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQDelayConfig_industry {

       public static final String DELAY_EXCHANGE = "delay_exchange";
       public static final String DELAY_QUEUE = "delay_queue";
       public static final String DELAY_ROUTING_KEY = "delay.refresh.industry";
       @Bean(DELAY_EXCHANGE)
       public Exchange exchange(){
              return ExchangeBuilder.topicExchange(DELAY_EXCHANGE)
                      .durable(true)
                      .delayed()      // 开启延迟队列
                      .build();
       }

       @Bean(DELAY_QUEUE)
       public Queue queue(){
              return QueueBuilder
                      .durable(DELAY_QUEUE)
                      .build();
       }


       @Bean
       public Binding delayBinding(@Qualifier(DELAY_QUEUE) Queue queue,@Qualifier(DELAY_EXCHANGE) Exchange exchange){
           return   BindingBuilder.bind(queue).to(exchange).with("delay.refresh.#").noargs();
       }


       //设置消息属性，延时时间
       public static MessagePostProcessor messagePostProcessor(Integer time){
              return message -> {
                     message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                     message.getMessageProperties().setDelay(time); // 延时10秒
                     return message;
              };
       }
}

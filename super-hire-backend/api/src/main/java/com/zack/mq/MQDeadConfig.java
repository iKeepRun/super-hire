package com.zack.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列配置
 */
@Configuration
public class MQDeadConfig {

       public static final String DEAD_SMS_EXCHANGE = "dead-sms-exchange";
       public static final String DEAD_SMS_QUEUE = "dead-sms-queue";
       public static final String DEAD_ROUTING_KEY = "dead.sms.display";

       @Bean(DEAD_SMS_EXCHANGE)
       public Exchange deadExchange(){
              return ExchangeBuilder.topicExchange(DEAD_SMS_EXCHANGE).durable(true).build();
       }

       @Bean(DEAD_SMS_QUEUE)
       public Queue deadQueue(){
              return QueueBuilder.durable(DEAD_SMS_QUEUE)
                      .build();
       }


       @Bean
       public Binding deadBinding(@Qualifier(DEAD_SMS_QUEUE) Queue queue,@Qualifier(DEAD_SMS_EXCHANGE) Exchange exchange){
           return   BindingBuilder.bind(queue).to(exchange).with("dead.sms.#").noargs();
       }

}

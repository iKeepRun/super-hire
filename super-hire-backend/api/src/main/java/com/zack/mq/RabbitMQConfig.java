package com.zack.mq;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置类
 */
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "sms_queue";
    public static final String EXCHANGE_NAME = "sms_exchange";
    public static final String ROUTING_KEY = "sms.send.login";

    //创建交换机
    @Bean(EXCHANGE_NAME)
    public Exchange createExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    //创建队列
    @Bean(QUEUE_NAME)
    public Queue createQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    //绑定队列和交换机
    @Bean
    public Binding createBinding(@Qualifier(QUEUE_NAME) Queue queue,@Qualifier(EXCHANGE_NAME) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY).noargs();
    }
}

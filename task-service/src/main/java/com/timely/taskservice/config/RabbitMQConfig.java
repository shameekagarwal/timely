package com.timely.taskservice.config;

import com.timely.taskservice.constant.ProfilesConstants;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ ProfilesConstants.PROD, ProfilesConstants.DEV, ProfilesConstants.QA })
public class RabbitMQConfig {

    private final String projectSaveQueueName;
    private final String projectDeleteQueueName;
    private final String projectSaveBindingKey;
    private final String projectDeleteBindingKey;
    private final String exchangeName;

    public RabbitMQConfig(@Value("${custom.rabbitmq.project-save-queue}") String projectSaveQueueName,
            @Value("${custom.rabbitmq.project-delete-queue}") String projectDeleteQueueName,
            @Value("${custom.rabbitmq.project-save-binding}") String projectSaveBindingKey,
            @Value("${custom.rabbitmq.project-delete-binding}") String projectDeleteBindingKey,
            @Value("${custom.rabbitmq.exchange}") String exchangeName) {
        this.projectSaveQueueName = projectSaveQueueName;
        this.projectDeleteQueueName = projectDeleteQueueName;
        this.projectSaveBindingKey = projectSaveBindingKey;
        this.projectDeleteBindingKey = projectDeleteBindingKey;
        this.exchangeName = exchangeName;
    }

    @Bean
    public Queue projectSaveQueue() {
        return new Queue(projectSaveQueueName);
    }

    @Bean
    public Queue projectDeleteQueue() {
        return new Queue(projectDeleteQueueName);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding projectSaveBinding(Queue projectSaveQueue, DirectExchange exchange) {
        return BindingBuilder.bind(projectSaveQueue).to(exchange).with(projectSaveBindingKey);
    }

    @Bean
    public Binding projectDeleteBinding(Queue projectDeleteQueue, DirectExchange exchange) {
        return BindingBuilder.bind(projectDeleteQueue).to(exchange).with(projectDeleteBindingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

}

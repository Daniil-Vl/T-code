package ru.tbank.contestservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessagingConfig {

    @Bean
    public Queue submissionQueue(ApplicationConfig applicationConfig) {
        QueueBuilder queueBuilder;

        if (applicationConfig.rabbitMq().isDurable()) {
            queueBuilder = QueueBuilder.durable(applicationConfig.rabbitMq().submissionQueueName());
        } else {
            queueBuilder = QueueBuilder.nonDurable(applicationConfig.rabbitMq().submissionQueueName());
        }

        return queueBuilder
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", applicationConfig.rabbitMq().submissionDeadLetterQueueName())
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitProducerMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter rabbitProducerMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitProducerMessageConverter);
        return rabbitTemplate;
    }

}

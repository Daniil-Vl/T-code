package ru.tbank.submissionservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MessagingConfig implements RabbitListenerConfigurer {
    private final LocalValidatorFactoryBean validator;

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
    public Queue deadLetterQueue(ApplicationConfig applicationConfig) {
        return QueueBuilder.
                durable(applicationConfig.rabbitMq().submissionDeadLetterQueueName())
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitProducerMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Override
    public void configureRabbitListeners(
            RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar
    ) {
        rabbitListenerEndpointRegistrar.setValidator(validator);
    }

}

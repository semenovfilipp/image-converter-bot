package org.semenov.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Этот класс представляет собой конфигурацию для RabbitMQ
 * В этом классе определен bean jsonMessageConverter, который является конвертером сообщений.
 * Когда происходит отправка сообщений в RabbitMQ, они должны быть сконвертированы в формат, понятный RabbitMQ.
 * В данном случае, используется JSON формат для сообщений.
 */
@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}


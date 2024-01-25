package org.semenov.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/*
 * Сервис для отправки ответов из node в RabbitMQ
 */
public interface ProducerService {
    void  produceAnswer(SendMessage sendMessage);
}

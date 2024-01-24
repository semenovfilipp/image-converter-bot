package org.semenov.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/*
* Принимает ответы из RabbitMQ
* Далее передает их в UpdateController
 */
public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
}
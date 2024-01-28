package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import org.semenov.controller.UpdateProcessor;
import org.semenov.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.semenov.RabbitQueue.ANSWER_MESSAGE;
/*
 * Сервис принимает сообщения в виде ответов из сервиса Node
 * Далее передает их в UpdateController
 */
@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}

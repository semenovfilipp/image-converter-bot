package org.semenov.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/*
* Передает update в RabbitMQ
 */
public interface UpdateProducer {
    void produce(String rabbitQueue, Update update);
}

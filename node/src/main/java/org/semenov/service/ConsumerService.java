package org.semenov.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/*
 * Сервис для считывания сообщений из брокера
 */
public interface ConsumerService {
    void consumeTextMessageUpdate(Update update);
    void consumeDocMessageUpdate(Update update);
    void consumePhotoMessageUpdate(Update update);
}

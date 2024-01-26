package org.semenov.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/*
 * Отвечает за обработку всех входящих сообщений
 */
public interface MainService {
    void processTextMessage(Update update);
    void processPhotoMessage(Update update);
    void processDocMessage(Update update);
}

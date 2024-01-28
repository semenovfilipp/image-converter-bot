package org.semenov.controller;


import lombok.extern.log4j.Log4j;
import org.semenov.service.UpdateProducer;
import org.semenov.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.semenov.RabbitQueue.*;
/*
 * Контроллер распределяет входящие сообщения из Телеграм бота.
 * Для разных типов сообщений класс передает разный набор входящих параметров
 */


@Component
@Log4j
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateProcessor(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }


    /*
     * Внедрение зависимости
     * @param telegramBot
     */
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    /*
     * Распределение входящего сообщения из телеграм бота.
     */
    public void precessUpdate(Update update) {
        if (update == null) {
            log.error("Received update is not null");
            return;
        }

        if (update.getMessage() != null) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }


    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocumentMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }

    }




    /*
     * Отправление промежуточного сообщения пользователю
     */
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        log.info("File received: " + update);
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "File received.Please stand by...");

        setView(sendMessage);

    }
    private void setUnsupportedMessageTypeView(Update update) {
        log.error("Received unsupported message type: " + update);
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type");

        setView(sendMessage);
    }

    /*
     * Отправление сообщений в очередь RabbitMQ
     */

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }


    private void processDocumentMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
}




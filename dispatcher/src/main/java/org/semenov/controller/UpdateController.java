package org.semenov.controller;


import javassist.compiler.ast.Pair;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.semenov.service.UpdateProducer;
import org.semenov.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

import static org.semenov.RabbitQueue.*;
/*
 * Класс распределяет входящие сообщения от телеграм бота.
 * Для разных типов сообщений класс передает разный набор входящих параметров
 */


@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
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

    private void setUnsupportedMessageTypeView(Update update) {
        log.error("Received unsupported message type: " + update);
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type");

        setView(sendMessage);
    }


    /*
     * Отправление сообщения пользователю
     * Отправление промежуточного сообщения пользователю
     */
    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        log.info("File received: " + update);
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "File received.Please stand by...");

        setView(sendMessage);

    }

    /*
     * Передача сообщений в очередь
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




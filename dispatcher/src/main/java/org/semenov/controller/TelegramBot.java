package org.semenov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    private final UpdateProcessor updateProcessor;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

//    @Value("${bot.uri}")
//    private String botUri;


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }



    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    /*
     * Передача входящего сообщения в контроллер
     */
    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
//        try{
//            var setWebhook = SetWebhook.builder()
//                    .url(botUri)
//                    .build();
//            this.setWebhook(setWebhook);
//        } catch (TelegramApiException e) {
//            log.error(e);
//        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.precessUpdate(update);
    }


//    @Override
//    public String getBotPath() {
//        return "/update";
//    }


}

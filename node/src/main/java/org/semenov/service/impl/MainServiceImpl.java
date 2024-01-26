package org.semenov.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.semenov.dao.AppUserDao;
import org.semenov.dao.RawDataDao;
import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.entity.AppUser;
import org.semenov.entity.RawData;
import org.semenov.entity.enums.UserState;
import org.semenov.exception.UploadFileException;
import org.semenov.service.FileService;
import org.semenov.service.MainService;
import org.semenov.service.ProducerService;
import org.semenov.service.enums.ServiceCommand;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.semenov.entity.enums.UserState.BASIC_STATE;
import static org.semenov.service.enums.ServiceCommand.*;

/*
 * Отвечает за обработку всех входящих сообщений
 */
@Service
@RequiredArgsConstructor
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = "";


        var serviceCommand = ServiceCommand.valueOf(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (userState.equals(BASIC_STATE)) {
            output = serviceCommandProcess(appUser, text);
        } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // TODO добавить обработку email
        } else {
            log.error("Unknown state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel для отмены операции";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            // TODO добавить генерацию ссылки
            var answer = "Фотография загружена!" +
                    "Ссылка для скачивания: " +
                    "https://drive.google.com/file/d/1-72l-456-789-0987";

            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error("Error during photo processing", e);
            String error = "Произошла ошибка при загрузке фотографии. ";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument document = fileService.processDoc(update.getMessage());

            // TODO формирование ссылки на скачивание
            var answer = "Документ загружен!" +
                    "Ссылка для скачивания: " +
                    "https://drive.google.com/file/d/1-72l-456-789-09";

            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "К сожалению загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();

        if (!appUser.getIsActive()) {
            var error = "Зарегестрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;

        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую комманду с помощью /cancel";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        producerService.produceAnswer(sendMessage);
    }

    private String serviceCommandProcess(AppUser appUser, String command) {
        if (REGISTRATION.equals(command)) {
            // TODO добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(command)) {
            return help();
        } else if (START.equals(command)) {
            return "Приветствую!Чтобы посмотреть список доступных комманд, введите /help";
        } else {
            return "Неизвестная комманда!Чтобы посмотреть список доступных комманд, введите /help";
        }
    }

    private String help() {
        return "Доступные команды:\n" +
                "/start - показать список доступных команд\n" +
                "/help - показать список доступных команд\n" +
                "/registration - регистрация нового пользователя\n" +
                "/cancel - отмена операции";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Комманда отменена!";
    }


    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());

        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstname(telegramUser.getFirstName())
                    .lastname(telegramUser.getLastName())
                    // TODO изменить значение
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();

            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    public void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();

        rawDataDao.save(rawData);
    }
}

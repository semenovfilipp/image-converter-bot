package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.semenov.dao.AppUserDao;
import org.semenov.entity.AppUser;
import org.semenov.entity.enums.UserState;
import org.semenov.service.AppUserService;
import org.semenov.utils.CryptoTool;
import org.semenov.utils.dto.MailParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/*
 *  Сервис для регистрации пользователей и установки их электронной почты
 */

@Service
@RequiredArgsConstructor
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегестрированны";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту отправлено письмо для подтверждения регистрации.";
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Пожалуйста введите ваш email: ";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите пожалуйста корректный email. Для отмены команды введите /cancel";
        }

        var optional = appUserDao.findUserByEmail(email);
        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setUserState(UserState.BASIC_STATE);
            appUserDao.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var response = sendRequestToEmailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var message = String.format("Произошла ошибка при отправке сообщения на почту %s", email);
                log.error(message);

                appUser.setEmail(null);
                appUserDao.save(appUser);
                return message;
            }
            return "Вам на почту было отправленно письмо."
                    + "Пожалуйста передите по ссылке в письме для подтверждения активации";
        } else {
            return "Пользователь с таким email уже существует. Для отмены введите /cancel";
        }
    }
    /*
     * Данный метод отправляет запрос на сервис
     * отправки электронной почты (mail-service), используя RestTemplate
     * @param cryptoUserId - хеш id пользователя
     */

    private ResponseEntity<String> sendRequestToEmailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<MailParams>(mailParams, headers);

        return restTemplate.exchange(
                mailServiceUri,
                HttpMethod.POST,
                request,
                String.class
        );
    }
}

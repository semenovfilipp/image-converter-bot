package org.semenov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Сервис отправляется в БД
 * Выгружает оттуда нужный контент
 * Активирует пользователя при переходе по ссылке
 */
@SpringBootApplication
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}

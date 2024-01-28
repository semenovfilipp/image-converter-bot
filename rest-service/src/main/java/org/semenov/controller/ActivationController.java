package org.semenov.controller;

import lombok.RequiredArgsConstructor;
import org.semenov.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Контроллер для активации пользователй по ссылке из письма
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ActivationController {
    private final UserActivationService activationService;

    @RequestMapping(method = RequestMethod.GET, value = "activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        var result = activationService.activation(id);
        if (result){
            return ResponseEntity.ok("Активация прошла успешно");
        } else {
            return ResponseEntity.badRequest().body("Не удалось активировать пользователя");
        }

    }
}

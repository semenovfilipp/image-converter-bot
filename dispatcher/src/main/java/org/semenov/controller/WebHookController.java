package org.semenov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
/*
 * Контроллер для  обработки web-хуков
 */

@RestController
@RequiredArgsConstructor
public class WebHookController {
    private final UpdateProcessor updateProcessor;

    @RequestMapping(value = "/callback/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceived(
            @RequestBody Update update
    ) {
        updateProcessor.precessUpdate(update);
        return ResponseEntity.ok().build();

    }
}

package org.semenov.service;

import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long documentId, LinkType linkType);


}

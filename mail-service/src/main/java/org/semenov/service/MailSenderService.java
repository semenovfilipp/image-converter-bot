package org.semenov.service;

import org.semenov.dto.MailParams;

public interface MailSenderService {
    void sendMail(MailParams mailParams);
}

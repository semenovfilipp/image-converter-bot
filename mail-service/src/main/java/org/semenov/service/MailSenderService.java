package org.semenov.service;

import org.semenov.utils.dto.MailParams;

public interface MailSenderService {
    void sendMail(MailParams mailParams);
}

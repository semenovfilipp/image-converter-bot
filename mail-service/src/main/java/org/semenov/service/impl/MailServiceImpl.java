package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import org.semenov.dto.MailParams;
import org.semenov.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${service.activation.uri}")
    private String activationUri;

    @Override
    public void sendMail(MailParams mailParams) {
        var subject = "Активация учетной записи.";
        var messageBody = getActivationMailBody(mailParams.getId());
        var emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        var message = String.format("Для активации учетной записи перейдите по ссылке: \n%s",
                activationUri);

        return message.replace("{id}", id);
    }
}

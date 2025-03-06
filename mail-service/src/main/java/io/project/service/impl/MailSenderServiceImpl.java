package io.project.service.impl;

import io.project.dto.MailParams;
import io.project.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    private final JavaMailSender mailSender;

    @Override
    public void send(MailParams mailParams) {
        String subject = "Account activation";
        String messageBody = getActivationMessageBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        mailSender.send(mailMessage);
    }

    private String getActivationMessageBody(String id) {
        String message = "To finish the registration follow the link:\n%s".formatted(activationServiceUri);
        return message.replace("{id}", id);
    }
}

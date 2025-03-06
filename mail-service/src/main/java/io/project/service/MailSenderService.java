package io.project.service;

import io.project.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);
}

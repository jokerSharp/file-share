package io.project.service;

import io.project.dto.MailParams;

public interface MailSender {

    void send(MailParams mailParams);
}

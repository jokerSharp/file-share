package io.project.service;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {

    AppDocument processDocument(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long id, LinkType linkType);
}

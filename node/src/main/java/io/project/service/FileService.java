package io.project.service;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {

    AppDocument processDocument(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}

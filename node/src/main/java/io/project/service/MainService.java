package io.project.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {

    void processTextMessage(Update update);
    void processDocumentMessage(Update update);
    void processPhotoMessage(Update update);
}

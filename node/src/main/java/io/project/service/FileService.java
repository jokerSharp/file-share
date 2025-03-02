package io.project.service;

import io.project.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {

    AppDocument processDocument(Message externalMessage);
}

package io.project.controller;

import io.project.service.UpdateProducer;
import io.project.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static io.project.commonrabbitmq.model.RabbitQueue.*;

@RequiredArgsConstructor
@Log4j2
@Component
public class UpdateController {

    private static final String unsupportedMessageType = "Unsupported message type";
    private static final String fileReceived = "File is received and processing";

    private TelegramBot telegramBot;
    private final UpdateProducer updateProducer;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }
        if (update.getMessage() != null) {
            distributeMessageByType(update);
        }
    }

    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeViewMessage(update);
        }
    }

    private void setUnsupportedMessageTypeViewMessage(Update update) {
        SendMessage sendMessage = MessageUtils.generateSendMessageWithText(update, unsupportedMessageType);
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        SendMessage sendMessage = MessageUtils.generateSendMessageWithText(update, fileReceived);
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}

package io.project.service.impl;

import io.project.dao.AppUserDAO;
import io.project.dao.RawDataDAO;
import io.project.entity.*;
import io.project.exeption.UploadFileException;
import io.project.service.FileService;
import io.project.service.MainService;
import io.project.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static io.project.entity.UserState.BASIC_STATE;
import static io.project.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static io.project.service.enums.ServiceCommand.*;

@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private static final Logger log = LoggerFactory.getLogger(MainServiceImpl.class);
    private final RawDataDAO rawDataDAO;
    private final ProducerServiceImpl producerServiceImpl;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";

        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //todo implement after registration
        } else {
            log.error("Unknown state: {}", userState);
            output = "Unknown error, please input /cancel and try again";
        }

        long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocumentMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        long chatId = update.getMessage().getChatId();

        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument document = fileService.processDocument(update.getMessage());
            //todo implement
            String output = "Document is successfully processed. Url for downloading: http://test.com/get-doc/123";
            sendAnswer(output, chatId);
        } catch (UploadFileException e) {
            log.error(e.getMessage());
            String error = "Unfortunately, there was an error processing the document. Please try again later";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        long chatId = update.getMessage().getChatId();

        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            //todo implement
            String output = "Photo is successfully processed. Url for downloading: http://test.com/get-doc/123";
            sendAnswer(output, chatId);
        } catch (UploadFileException e) {
            log.error(e.getMessage());
            String error = "Unfortunately, there was an error processing the photo. Please try again later";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowedToSendContent(long chatId, AppUser appUser) {
        UserState userState = appUser.getState();
        if (!appUser.getIsActive()) {
            String error = "Please activate your account first";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            String error = "Unknown error, please type /cancel and try again";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, long chatId) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(output).build();
        producerServiceImpl.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String command) {
        if (REGISTRATION.equals(ServiceCommand.fromValue(command))) {
            return "Temporary unavailable";
        } else if (HELP.equals(ServiceCommand.fromValue(command))) {
            return help();
        } else if (START.equals(ServiceCommand.fromValue(command))) {
            return "Welcome! To get the list of available commands type /help";
        } else {
            return "Unknown command! To get the list of available commands type /help";
        }
    }

    private String help() {
        return """
                List of available commands:
                /cancel - cancelling current command;
                /registration - user registration.""";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Command is cancelled";
    }

    private AppUser findOrSaveAppUser(Update update) {
        Message textMessage = update.getMessage();
        User telegramUser = textMessage.getFrom();
        AppUser persistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //todo change after registration implementation
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDAO.save(rawData);
    }
}

package io.project.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Log4j2
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String token;

    private final TelegramClient telegramClient;
    private final UpdateController updateController;

    public TelegramBot(@Value("${bot.token}") String token, UpdateController updateController) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public void consume(Update update) {
        updateController.processUpdate(update);
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending message", e);
            }
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}

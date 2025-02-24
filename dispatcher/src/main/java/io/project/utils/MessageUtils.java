package io.project.utils;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@UtilityClass
public class MessageUtils {

    public static SendMessage generateSendMessageWithText(Update update, String text) {
        Message message = update.getMessage();
        return SendMessage.builder().chatId(message.getChatId()).text(text).build();
    }
}

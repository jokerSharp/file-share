package io.project.service.impl;

import io.project.dao.RawDataDAO;
import io.project.entity.RawData;
import io.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerServiceImpl producerServiceImpl;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        Message message = update.getMessage();
        SendMessage sendMessage = SendMessage.builder().chatId(message.getChatId()).text("Hello from node").build();
        producerServiceImpl.producerAnswer(sendMessage);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDAO.save(rawData);
    }
}

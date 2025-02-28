package io.project.service.impl;

import io.project.dao.RawDataDAO;
import io.project.entity.RawData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MainServiceImplTest {

    @Autowired
    private RawDataDAO rawDataDAO;

    @Test
    void saveRawData() {
        Update update = new Update();
        Message message = new Message();
        message.setText("Hello World");
        update.setMessage(message);

        RawData rawData = RawData.builder().event(update).build();

        Set<RawData> testData = Set.of(rawData);
        rawDataDAO.save(rawData);
        assertTrue(testData.contains(rawData), "Entity is not found in the set");
    }
}
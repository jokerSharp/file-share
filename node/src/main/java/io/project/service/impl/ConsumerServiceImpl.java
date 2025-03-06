package io.project.service.impl;

import io.project.service.ConsumerService;
import io.project.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static io.project.commonrabbitmq.model.RabbitQueue.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    @Override
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: TEXT message is received");

        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    @Override
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: DOC message is received");

        mainService.processDocumentMessage(update);
    }

    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    @Override
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: PHOTO message is received");

        mainService.processPhotoMessage(update);
    }
}

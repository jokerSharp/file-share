package io.project.service.impl;

import io.project.controller.UpdateController;
import io.project.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    @Override
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}

package io.project.service.impl;

import io.project.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(answerMessageQueue, sendMessage);
    }
}

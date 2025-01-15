package org.deal.service;

import lombok.extern.slf4j.Slf4j;
import org.deal.dto.EmailMessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, EmailMessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendMessage(String topic, EmailMessageDto message) {
        log.info("Отправка сообщения брокеру {}, {}, {}", message.getAdress(), message.getStatementId(), message.getText());
        kafkaTemplate.send(topic, message);
    }
}

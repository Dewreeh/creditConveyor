package org.deal.service;

import org.deal.dto.EmailMessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, EmailMessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendMessage(String topic, EmailMessageDto message) {

        kafkaTemplate.send(topic, message);
    }
}

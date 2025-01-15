package org.dossier.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "finish-registration", groupId = "dossier-consumer-group")
    public void listenFinishRegistration(String message) {
        log.info("Получено сообщение в топике finish-registration : {}", message);
    }

    @KafkaListener(topics = "create-documents", groupId = "dossier-consumer-group")
    public void listenCreateDocuments(String message) {
        log.info("Получено сообщение в топике create-documents: {}", message);
    }

    @KafkaListener(topics = "send-documents", groupId = "dossier-consumer-group")
    public void listenSendDocuments(String message) {
        log.info("Получено сообщение в топике send-documents: {}", message);
    }

    @KafkaListener(topics = "send-ses", groupId = "dossier-consumer-group")
    public void listenSendSes(String message) {
        log.info("Получено сообщение в топике send-ses: {}", message);

    }

    @KafkaListener(topics = "credit-issued", groupId = "dossier-consumer-group")
    public void listenCreditIssued(String message) {
        log.info("Получено сообщение в топике 'credit-issued': {}", message);
    }

}

package org.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dossier.dto.EmailMessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "finish-registration", groupId = "dossier-consumer-group")
    public void listenFinishRegistration(String message) {
        try {
            EmailMessageDto dto = objectMapper.readValue(message, EmailMessageDto.class);
            log.info("Получено сообщение в топике finish-registration: {}\n {}", dto, message);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из finish-registration: {}", message, e);
        }
    }

    @KafkaListener(topics = "create-documents", groupId = "dossier-consumer-group")
    public void listenCreateDocuments(String message) {
        try {
            EmailMessageDto dto = objectMapper.readValue(message, EmailMessageDto.class);
            log.info("Получено сообщение в топике create-documents: {}", dto);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из create-documents: {}", message, e);
        }
    }

    @KafkaListener(topics = "send-documents", groupId = "dossier-consumer-group")
    public void listenSendDocuments(String message) {
        try {
            EmailMessageDto dto = objectMapper.readValue(message, EmailMessageDto.class);
            log.info("Получено сообщение в топике send-documents: {}", dto);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из send-documents: {}", message, e);
        }
    }

    @KafkaListener(topics = "ses-code", groupId = "dossier-consumer-group")
    public void listenSendSes(String message) {
        try {
            EmailMessageDto dto = objectMapper.readValue(message, EmailMessageDto.class);
            log.info("Получено сообщение в топике send-ses: {}", dto);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из send-ses: {}", message, e);
        }
    }

    @KafkaListener(topics = "credit-issued", groupId = "dossier-consumer-group")
    public void listenCreditIssued(String message) {
        try {
            EmailMessageDto dto = objectMapper.readValue(message, EmailMessageDto.class);
            log.info("Получено сообщение в топике credit-issued: {}", dto);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из credit-issued: {}", message, e);
        }
    }

}

package org.deal.controller;

import org.deal.dto.EmailMessageDto;
import org.deal.enums.Theme;
import org.deal.model.Statement;
import org.deal.repository.StatementRepository;
import org.deal.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.UUID;

@Controller
@RequestMapping("deal/document")
public class DocumentController {

    private final KafkaProducerService kafkaProducerService;
    private final StatementRepository statementRepository; //чтобы по заявке достать клиента и его почту

    @Autowired
    public DocumentController(KafkaProducerService kafkaProducerService, StatementRepository statementRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.statementRepository = statementRepository;
    }

    @PostMapping("{statementId}/send")
    public ResponseEntity<Object> send(@PathVariable("statementId") UUID statementId){
        //Не было сказано про логику формирования документов, потому этот и последующие хэндлеры
        //по сути просто заглушки для имитации логики работы системы

        //по заявке получаем почту клиента
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();

        kafkaProducerService.sendMessage("send-documents", new EmailMessageDto(
                email,
                Theme.SEND_DOCUMENTS,
                statementId,
                "Ваш ПЭП-код ****"
        ));

        return ResponseEntity.ok().body("Формируются документы");
    }

    @PostMapping("{statementId}/sign")
    public ResponseEntity<Object> sign(@PathVariable("statementId") UUID statementId){
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();

        kafkaProducerService.sendMessage("credit-issued", new EmailMessageDto(
                email,
                Theme.SEND_SES,
                statementId,
                "Кредит выдан"
        ));
        return ResponseEntity.ok().body("Документ подписан");
    }

    @PostMapping("{statementId}/code")
    public ResponseEntity<Object> code(@PathVariable("statementId") UUID statementId){
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();

        kafkaProducerService.sendMessage("ses-code", new EmailMessageDto(
                email,
                Theme.SEND_SES,
                statementId,
                "Ваш ПЭП-код ****"
        ));

        return ResponseEntity.ok().body("Запрос отправлен");
    }
}

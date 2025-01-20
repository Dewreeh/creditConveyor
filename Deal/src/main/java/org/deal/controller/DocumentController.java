package org.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    @Operation(
            summary = "Запрос на отправку документов брокеру",
            description = "Отправляет брокеру запрос на отправку документов для МС dossier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Формируются документы",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("{statementId}/send")
    public ResponseEntity<Object> send(@PathVariable("statementId") UUID statementId){
        //Не было сказано про логику формирования документов, потому этот и последующие хэндлеры
        //по сути просто заглушки для имитации логики работы системы

        //по заявке получаем почту клиента
        Statement statement = statementRepository.getByStatementId(statementId);

        log.info("Получен запрос на отправку документов по заявке {}", statementId);
        String email = statement.getClient().getEmail();
        log.info("Получена почта клиента по заявке {}: ", statementId);
        kafkaProducerService.sendMessage("send-documents", new EmailMessageDto(
                email,
                Theme.SEND_DOCUMENTS,
                statementId,
                "Ваш ПЭП-код ****"
        ));


        return ResponseEntity.ok().body("Формируются документы");
    }

    @Operation(
            summary = "Запрос на подпись документов брокеру",
            description = "Отправляет брокеру запрос на подпись документов для МС dossier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Документ подписан",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("{statementId}/sign")
    public ResponseEntity<Object> sign(@PathVariable UUID statementId){
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();

        UUID sesCode = UUID.randomUUID();
        statement.setSesCode(sesCode); //генерируем ses-code

        statementRepository.save(statement); //сохраняем в statement сгенерированный sesCode;

        kafkaProducerService.sendMessage("send-ses", new EmailMessageDto(
                email,
                Theme.SEND_DOCUMENTS,
                statementId,
                "Ваш ses-code: {} \n " + sesCode
                        + "Ссылка на подписание: http://localhost:8120/gateway/deal/document/{}/{}" + statementId + sesCode
        ));
        return ResponseEntity.ok().body("SecCode сформирован, ссылка на подписание отправлена");
    }

    @Operation(
            summary = "Запрос на отправку кода ПЭП брокеру",
            description = "Отправляет брокеру запрос на отправку кода ПЭП для МС dossier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Код ПЭП",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
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
        log.info("Получен запрос на отправку кода ПЭП по заявке {}", statementId);
        return ResponseEntity.ok().body("Запрос отправлен");
    }
}

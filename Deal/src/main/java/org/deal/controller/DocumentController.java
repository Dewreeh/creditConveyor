package org.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.deal.dto.EmailMessageDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.CreditStatus;
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

import javax.swing.plaf.nimbus.State;
import java.nio.file.Path;
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
    @PostMapping("{statementId}/send")
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
    public ResponseEntity<Object> send(@PathVariable("statementId") UUID statementId){

        //по заявке получим почту клиента
        Statement statement = statementRepository.getByStatementId(statementId);

        log.info("Получен запрос на отправку документов по заявке {}", statementId);

        String email = statement.getClient().getEmail();

        log.info("Получена почта клиента по заявке {}: ", statementId);

        kafkaProducerService.sendMessage("send-documents", new EmailMessageDto(
                email,
                Theme.SEND_DOCUMENTS,
                statementId,
                ("Документы сформированы. Ссылка на согласие: " + "http://localhost:8120/gateway/deal/documents/" + statementId +"/sign")
        ));

        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);

        return ResponseEntity.ok().body("Формируются документы");
    }

    @PostMapping("{statementId}/sign")
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
    public ResponseEntity<Object> sign(@PathVariable("statementId") UUID statementId){
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();

        UUID ses_code = UUID.randomUUID(); //генерируем уникальный ПЭП код

        statement.setSesCode(ses_code);

        statementRepository.save(statement);

        kafkaProducerService.sendMessage("ses-code", new EmailMessageDto(
                email,
                Theme.SEND_SES,
                statementId,
                ("Получен запрос на подписание документов по заявке, ваш ПЭП код: " + ses_code + "\n" +
                        "Подпишите по ссылке: " + "http://localhost:8120/gateway/deal/document/" + statementId + "/" + ses_code)
                ));

        log.info("Получен запрос на подписание документов по заявке " + statement);
        return ResponseEntity.ok().body("Отправлен ses-code");
    }

    @PostMapping("{statementId}/{code}")
    @Operation(
            summary = "Запрос на отправку кода ПЭП брокеру",
            description = "Отправляет брокеру запрос на отправку сформированного кода ПЭП для МС dossier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Код ПЭП",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public ResponseEntity<Object> code(@PathVariable("statementId") UUID statementId,
                                       @PathVariable("code") UUID UserCode){
        Statement statement = statementRepository.getByStatementId(statementId);

        //сравниваем ПЭП код из БД и тот, что прислал клиент

        if(statement != null) {
            if (statement.getSesCode().equals(UserCode)) {
                statement.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
                statement.getCredit().setCreditStatus(CreditStatus.ISSUED);

                String email = statement.getClient().getEmail(); //получаем почту клиента

                kafkaProducerService.sendMessage("credit-issued", new EmailMessageDto(
                        email,
                        Theme.SEND_SES,
                        statementId,
                        "Ваш кредит оформлен!"
                ));
                log.info("Документ подписан!" + statementId);
                return ResponseEntity.ok().body("Документ успешно подписан!");
            }
            log.info("Не совпадает код ПЭП: " + statementId);
            return ResponseEntity.ok().body("Не совпадает код ПЭП");

        }
        return ResponseEntity.ok().body("Заявка не существует");
    }
}

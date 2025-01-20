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
import org.deal.repository.CreditRepository;
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
    private final CreditRepository creditRepository;

    @Autowired
    public DocumentController(KafkaProducerService kafkaProducerService, StatementRepository statementRepository, CreditRepository creditRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
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
    public ResponseEntity<Object> send(@PathVariable UUID statementId){

        //по заявке получаем почту клиента
        Statement statement = statementRepository.getByStatementId(statementId);

        String email = statement.getClient().getEmail();


        kafkaProducerService.sendMessage("send-documents", new EmailMessageDto(
                email,
                Theme.SEND_DOCUMENTS,
                statementId,
                "Вот ваши документы: \n Ссылка на согласие: http://localhost:8120/gateway/deal/document/{}" + statementId + "/sign"
        ));

        return ResponseEntity.ok().body("Документы формируются");
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
    public ResponseEntity<Object> code(@PathVariable("statementId") UUID statementId,
                                       @PathVariable("code") UUID sesCode){

        Statement statement = statementRepository.getByStatementId(statementId);

        if(statement.getSesCode() == sesCode){
            statement.setStatus(ApplicationStatus.CREDIT_ISSUED);
        }

        statement.getCredit().setCreditStatus(CreditStatus.ISSUED);
        creditRepository.save(statement.getCredit());


        String email = statement.getClient().getEmail(); //получаем почту пользователя

        kafkaProducerService.sendMessage("credit-issued", new EmailMessageDto(
                email,
                Theme.SEND_SES,
                statementId,
                "Кредит выдан! "
        ));

        return ResponseEntity.ok().body("Запрос отправлен");
    }
}

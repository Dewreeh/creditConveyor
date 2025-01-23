package org.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.gateway.dto.FinishRegistrationRequestDto;
import org.gateway.dto.LoanOfferDto;
import org.gateway.dto.LoanStatementRequestDto;
import org.gateway.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final RestService restService;

    @Autowired
    public GatewayController(RestService restService) {
        this.restService = restService;
    }


    // deal/statement и /deal/offer/select здесь нет, т.к. они вызываются через statement


    @PostMapping("/deal/calculate{statementId}")
    @Operation(summary = "Рассчитать кредит", description = "Выполняет расчет кредита по заявке.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный расчет"),
            @ApiResponse(responseCode = "422", description = "Скоринг не пройден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<String> dealCalculateAPI(@RequestParam("statementId") UUID statementId,
                                            @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto){
        log.info("Получен запрос на подсчёт кредита по API gateway/deal/calculate по заявке {}", statementId);
        try {
            log.info("Запрос на подсчёт кредита по API gateway/deal/calculate по заявке {} успешно выполнен ", statementId);
            return restService.sendRequestToDealCalculate(statementId, finishRegistrationRequestDto);
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            log.info("Запрос на подсчёт кредита по API gateway/deal/calculate по заявке {} не прошёл скоринг", statementId);

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Скоринг не пройден");
        } catch (Exception e) {
            log.info("Ошибка при запросе на подсчёт кредита по API gateway/deal/calculate по заявке {}", statementId);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка на сервере");
        }
    }


    @PostMapping("/statement/statement")
    @Operation(summary = "Получить офферы", description = "Возвращает список доступных офферов.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Офферы получены"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<Object> statementStatementAPI(@RequestBody LoanStatementRequestDto loanStatementRequestDto){
        log.info("Получен запрос на получение офферов API gateway/statement/statement");
        try {
            List<LoanOfferDto> response = restService.sendRequestToStatementStatement(loanStatementRequestDto);
            log.info("Офферы получены по запросу на API gateway/statement/statement");
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            log.info("Ошибка получения офферов по запросу на API gateway/statement/statement");
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.info("Ошибка получения офферов по запросу на API gateway/statement/statement");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка на сервере: " + e.getMessage());
        }

    }

    @PostMapping("/statement/offer")
    @Operation(summary = "Выбрать оффер", description = "Сохраняет выбранный оффер.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Оффер сохранен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<String> statementOfferAPI(@RequestBody LoanOfferDto loanOfferDto){
        log.info("Получен запрос на выбор конкретного оффера по API gateway/statement/offer");
        try {
            log.info("Оффер сохранён по запросу на API gateway/statement/statement по заявке {}", loanOfferDto.getStatementId());
            return restService.sendRequestToStatementOffer(loanOfferDto);
        } catch (HttpClientErrorException e) {
            log.info("Ошибка при сохранении оффера по запросу на API gateway/statement/statement по заявке {}", loanOfferDto.getStatementId());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.info("Ошибка при сохранении оффера по запросу на API gateway/statement/statement по заявке {}", loanOfferDto.getStatementId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/send")
    @Operation(summary = "Формирование документов", description = "Генерирует документы по заявке.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Документы сформированы"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<String> dealDocumentSendApi(@PathVariable("statementId") UUID statementId){
        log.info("Получен запрос на формирование документов по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
        try {
            log.info("Сформированы документы по запросу на API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return restService.sendRequestToDealDocumentSend(statementId);
        } catch (HttpClientErrorException e) {
            log.info("Ошибка при формировании документов по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.info("Ошибка при формировании документов по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/sign")
    @Operation(summary = "Подписание документов", description = "Отправляет запрос на подписание документов. Результаттом является письмо с ПЭП-кодом на почту клиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отправлено письмо с ПЭП кодом на почту"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<String> dealDocumentSignApi(@PathVariable("statementId") UUID statementId){
        log.info("Получен запрос на подписание документов по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
        try {
            log.info("Отправлен запрос на MC dossier на формирование информации для подписания по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return restService.sendRequestToDealDocumentSign(statementId);
        } catch (HttpClientErrorException e) {
            log.info("Ошибка при запросе на MC dossier на формирование информации для подписания по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.info("Ошибка при запросе на MC dossier на формирование информации для подписания по API gateway/deal/document/{statementId}/send по заявке {}", statementId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/{code}")
    @Operation(summary = "Отправка кода ПЭП для подписи", description = "Отправляет ПЭП-код от клиента для подписания документов.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код принят, документы подписаны"),
            @ApiResponse(responseCode = "400", description = "Не совпал ПЭП код"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    ResponseEntity<String> dealDocumentSignApi(@PathVariable("statementId") UUID statementId,
                                               @PathVariable("code") UUID ses_code){
        log.info("Получен ПЭП код для подписания документов по API gateway/deal/document/{statementId}/{code} по заявке {}", statementId);
        try {
            log.info("Отправлен ПЭП в MC deal код для подписания документов по API gateway/deal/document/{statementId}/{code} по заявке {}", statementId);
            return restService.sendRequestToDealDocumentCode(statementId, ses_code);
        } catch (HttpClientErrorException e) {
            log.info("Ошибка при подписании  документов по API gateway/deal/document/{statementId}/{code} по заявке {}", statementId);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.info("Внутренняя ошибка сервера при подписании документов по API gateway/deal/document/{statementId}/{code} по заявке {}", statementId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

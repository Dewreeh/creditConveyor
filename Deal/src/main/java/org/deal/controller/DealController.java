package org.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.ScoringDataDto;
import org.deal.model.Client;
import org.deal.service.ClientService;
import org.deal.service.FinishRegistartionService;
import org.deal.service.SelectService;
import org.deal.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/deal")
public class DealController {
    private final StatementService statementService;
    private final SelectService selectService;
    private final ClientService clientService;
    private final FinishRegistartionService finishRegistartionService;

    @Autowired
    public DealController(StatementService statementService,
                          SelectService selectService,
                          FinishRegistartionService finishRegistartionService,
                          ClientService clientService) {

        this.statementService = statementService;
        this.selectService = selectService;
        this.finishRegistartionService = finishRegistartionService;
        this.clientService = clientService;
    }

    @PostMapping("/statement")
    @Operation(
            summary = "Зарегистрировать заявку на кредит и получить 4 кредитных предложения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка зарегистрирована, клиент зарегистрирован, сформированы 4 предложения",
                            content = @Content(schema = @Schema(implementation = LoanOfferDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка при обработке заявки"),
                    @ApiResponse(responseCode = "422", description = "Данные не прошли прескоринг")
            }
    )
    ResponseEntity<Object> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        log.info("Запрос на на API /statement, входные данные: {}", dto);
        List<LoanOfferDto> offers;
        try {
            // Получаем офферы
            offers = statementService.getOffers(dto);
            log.info("Получены офферы: {}", dto);

        } catch (ResponseStatusException e) {
            log.error("Ошибка при получении офферов: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        try {
            // Сохраняем клиента если офферы получены (прескоринг прошел)
            Client client = clientService.saveClient(dto);
            log.info("Клиент сохранён: {}", client);

            //сохраняем заявку со связью на клиента
            UUID statementUuid = statementService.saveStatement(client);
            log.info("Создана заявка с UUID: {}", statementUuid);

            //добавляем офферам id заявки
            offers = statementService.setUuidForOffers(offers, statementUuid);
            log.info("Офферам присвоен UUID заявки: {}", statementUuid);

        } catch (ResponseStatusException e) {
            log.error("Ошибка при сохранении клиента или заявки: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
        return ResponseEntity.ok(offers);
    }

    @Operation(
            summary = "Выбор конкретного кредитного предложения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Оффер выбран и сохранен в заявке"),
                    @ApiResponse(responseCode = "400", description = "Ошибка при выборе оффера")
            }
    )
    @PostMapping("/offer/select")
    ResponseEntity<Object> selectOffer(@Valid @RequestBody LoanOfferDto dto){
        log.info("Запрос на API /offer/select. Входные данные: {}", dto);
        try {
            selectService.applyOffer(dto);
            log.info("Установлен оффер: {}", dto);
            return ResponseEntity.ok("Успех");
        } catch (ResponseStatusException e){
            log.error("Ошибка при выборе оффера: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
    @PostMapping("/offer/calculate")
    @Operation(
            summary = "Завершение регистрации клиента и расчёт кредита",
            description = "Клиент отправляет дополнительные данные для завершения регистрации и проведения расчёта кредита. Обновляется заявка, в БД сохраняется кредит",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Кредит успешно рассчитан"),
                    @ApiResponse(responseCode = "400", description = "Ошибка при расчёте кредита")
            }
    )

    ResponseEntity<Object> calculate(@Valid @RequestBody FinishRegistrationRequestDto dto,
                                     @RequestParam("statementId") UUID statementId){
        log.info("Запрос на API /offer/calculate. Входные данные: {}, statementId: {}", dto, statementId);
        try {
            finishRegistartionService.finishRegistration(dto, statementId);
            log.info("Регистрация заявки завершена {}", statementId);
        } catch (ResponseStatusException e){
            log.error("Ошибка при расчёте кредита: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
        return ResponseEntity.ok("Кредит посчитан!");
    }
}

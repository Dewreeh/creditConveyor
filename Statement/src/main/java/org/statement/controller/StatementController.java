package org.statement.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.statement.dto.LoanOfferDto;
import org.statement.service.PrescoringService;
import org.statement.service.RestQueriesService;
import org.statement.dto.LoanStatementRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statement")
public class StatementController {

    private final PrescoringService prescoringService;
    private final RestQueriesService restQueriesService;

    @Autowired
    public StatementController(PrescoringService prescoringService,
                               RestQueriesService restQueriesService) {
        this.prescoringService = prescoringService;
        this.restQueriesService = restQueriesService;
    }

    @PostMapping()
    ResponseEntity<Object> statementHandler(@RequestBody LoanStatementRequestDto dto) {
        log.info("Запрос на API /statement. Входные данные: {}", dto);
        try {
            if (prescoringService.doPrescoring(dto)) {
                log.info("Прескоринг пройден. API /statement. Отправка запроса на получение офферов.");
                List<LoanOfferDto> offers = restQueriesService.getOffersFromDeal(dto);
                log.info("Офферы успешно получены через API /statement: {}", offers);
                return ResponseEntity.ok(offers);
            } else {
                log.warn("Прескоринг не пройден для данных через API /statement: {}", dto);
                return ResponseEntity.unprocessableEntity().body("Данные не прошли прескоринг, перепроверьте их и попробуйте снова");
            }
        } catch (ResponseStatusException e) {
            log.error("Ошибка при обработке офферов через API /statement: {}", e.getReason(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Неизвестная ошибка через API /statement: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/select/offer")
    ResponseEntity<Object> selectOffer(@Valid @RequestBody LoanOfferDto dto) {
        log.info("Запрос на API /statement/select/offer. Входные данные: {}", dto);
        try {
            restQueriesService.selectOffer(dto);
            log.info("Оффер успешно сохранён через API /statement/select/offer: {}", dto);
            return ResponseEntity.ok("Оффер успешно сохранён");
        } catch (Exception e) {
            log.error("Ошибка при сохранении оффера через API /statement/select/offer: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Произошла ошибка при выборе оффера");
        }
    }
}

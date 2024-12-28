package org.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.statement.dto.LoanOfferDto;
import org.statement.service.PrescoringService;
import org.statement.service.RestQueriesService;
import org.statement.dto.LoanStatementRequestDto;

import java.util.List;

@RestController
@RequestMapping("/statement")
public class StatementController {

    private final PrescoringService prescoringService;
    private final RestQueriesService restQueriesService;

    @Autowired
    public StatementController(PrescoringService prescoringService,
                               RestQueriesService restQueriesService){

        this.prescoringService = prescoringService;
        this.restQueriesService = restQueriesService;
    }


    @PostMapping()
    @Operation(
            summary = "Получение 4х кредитных предложений LoanOfferDto",
            description = "Проводит прескоринг данных клиента и возвращает доступные кредитные предложения, если прескоринг пройден",

            responses = {
                @ApiResponse(responseCode = "200", description = "Список кредитных предложений", content = @Content(schema = @Schema(implementation = LoanOfferDto[].class))),
                @ApiResponse(responseCode = "422", description = "Данные не прошли прескоринг"),
                @ApiResponse(responseCode = "500", description = "Произошла ошибка")
            }
    )
    ResponseEntity<Object> statementHandler(@RequestBody LoanStatementRequestDto dto){
        try {
            // Выполняем прескоринг
            if (prescoringService.doPrescoring(dto)) {
                List<LoanOfferDto> offers = restQueriesService.getOffersFromDeal(dto);
                return ResponseEntity.ok(offers);
            } else {
                return ResponseEntity.unprocessableEntity().body("Данные не прошли прескоринг, перепроверьте их и попробуйте снова");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Произошла ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/select/offer")
    @Operation(
            summary = "Выбор конкретного кредитного предложения",
            description = "Сохраняет выбранное кредитное предложение в заявку в БД",

            responses = {
                    @ApiResponse(responseCode = "200", description = "Оффер успешно сохранён"),
                    @ApiResponse(responseCode = "500", description = "Произошла ошибка при выборе оффера")
            }
    )
    ResponseEntity<Object> selectOffer(@Valid @RequestBody LoanOfferDto dto){
        try {
            restQueriesService.selectOffer(dto);
            return ResponseEntity.ok("Оффер успешно сохранён");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Произошла ошибка при выборе оффера");
        }
    }
}

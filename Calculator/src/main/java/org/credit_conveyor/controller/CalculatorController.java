package org.credit_conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.credit_conveyor.dto.CreditDto;
import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;

import org.credit_conveyor.dto.ScoringDataDto;
import org.credit_conveyor.service.CalcService;
import org.credit_conveyor.service.OffersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/calculator")
public class CalculatorController {
    @Autowired
    private OffersService offersService;

    @Autowired
    private CalcService calcService;

    @PostMapping("/offers")
    @Operation(
            summary = "Получить кредитные предложения",
            description = "Проводит прескоринг и возвращает список из 4 кредитных предложений на основе LoanStatementRequestDto",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "LoanOfferDto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoanStatementRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные прошли прескоринг и сформированы 4 кредитных предложения",
                            content = @Content(schema = @Schema(implementation = LoanOfferDto.class))),
                    @ApiResponse(responseCode = "422", description = "Данные не прошли прескоринг")
            }
    )

     ResponseEntity<Object> offers(@RequestBody LoanStatementRequestDto dto){
        log.info("Запрос на на API /offers, входные данные: {}", dto);

        List<LoanOfferDto> offers = offersService.getOffers(dto);

        log.info("Выходные данные по API /offers: {}", offers);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/calc")
    @Operation(
            summary = "Рассчитать параметры кредита",
            description = "Проводит скоринг и возвращает параметры кредита на основе ScoringDataDto",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ScoringDataDto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoanStatementRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Скоринг пройден, данные кредита сформированы",
                            content = @Content(schema = @Schema(implementation = CreditDto.class))),
                    @ApiResponse(responseCode = "422", description = "Скоринг не пройдён")
            }
    )

    ResponseEntity<Object> calc(@RequestBody ScoringDataDto dto){
        log.info("Запрос на API /calc, входные данные {}", dto);
        if(!calcService.isScoringDataOk(dto)){
            log.warn("Скоринг не пройден на API /calc: {}", dto);
            return ResponseEntity.unprocessableEntity().body("Отказ");
        }
        CreditDto credit = calcService.getCredit(dto);
        log.info("Выходные данные по API /calc: {}", credit);
        return ResponseEntity.ok(credit);
    }

}

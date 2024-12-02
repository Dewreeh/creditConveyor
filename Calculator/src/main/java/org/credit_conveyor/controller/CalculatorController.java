package org.credit_conveyor.controller;

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

@RestController
@RequestMapping("/calculator")
public class CalculatorController {
    @Autowired
    private OffersService offersService;
    @Autowired
    private CalcService calcService;
    @PostMapping("/offers")
     ResponseEntity<Object> offers(@RequestBody LoanStatementRequestDto dto){
        if(!offersService.isValid(dto)){
            return ResponseEntity.unprocessableEntity().body("Данные не прошли прескоринг. Пожалуйста, перепроверьте их и отправьте новый запрос");
        }
        return ResponseEntity.ok().body(offersService.getOffers(dto));
    }
    @PostMapping("/calc")
    ResponseEntity<Object> calc(@RequestBody ScoringDataDto dto){
        if(!calcService.isScoringDataOk(dto)){
            return ResponseEntity.unprocessableEntity().body("Отказ");
        }
        return ResponseEntity.ok().body(calcService.getCredit(dto));
    }
}

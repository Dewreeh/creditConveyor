package org.credit_conveyor.controller;

import org.credit_conveyor.dto.CreditDto;
import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.credit_conveyor.dto.ScoringDataDto;
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
    @PostMapping("/offers")
     ResponseEntity<?> offers(@RequestBody LoanStatementRequestDto dto){
        if(!offersService.isValid(dto)){
            return ResponseEntity.unprocessableEntity().body("Данные не прошли прескоринг. Пожалуйста, перепроверьте их и отправьте новый запрос");
        }
        return ResponseEntity.ok().body(offersService.getOffers(dto));
    }
    @PostMapping("/calc")
    CreditDto calc(@RequestBody ScoringDataDto dto){
        return null; //заглушка
    }
}

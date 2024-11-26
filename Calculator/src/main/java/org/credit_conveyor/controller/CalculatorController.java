package org.credit_conveyor.controller;

import org.credit_conveyor.dto.CreditDto;
import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.credit_conveyor.dto.ScoringDataDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    @PostMapping("/offers")
    List<LoanOfferDto> offers(@RequestBody LoanStatementRequestDto dto){
        return null; //заглушка
    }
    @PostMapping("/calc")
    CreditDto calc(@RequestBody ScoringDataDto dto){
        return null; //заглушка
    }
}
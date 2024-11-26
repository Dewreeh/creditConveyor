package org.example.controller;

import org.example.dto.CreditDto;
import org.example.dto.LoanOfferDto;
import org.example.dto.LoanStatementRequestDto;
import org.example.dto.ScoringDataDto;
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

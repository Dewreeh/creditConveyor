package org.deal.controller;

import jakarta.validation.Valid;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/deal")
public class DealController {
    private final StatementService statementService;

    @Autowired
    public DealController(StatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping("/statement")
    ResponseEntity<Object> getOffers(@Valid @RequestBody LoanStatementRequestDto dto){
        statementService.saveClient(dto);

        return ResponseEntity.ok(statementService.getOffers(dto));
    }

    @PostMapping("/offer/select")
    ResponseEntity<Object> selectOffer(@Valid @RequestBody LoanOfferDto dto){
        return ResponseEntity.ok("Успех"); //заглушка
    }
    @PostMapping("/offer/calculate")
    ResponseEntity<Object> calculate(@Valid @RequestBody FinishRegistrationRequestDto dto){
        return ResponseEntity.ok("Успех"); //заглушка
    }


}

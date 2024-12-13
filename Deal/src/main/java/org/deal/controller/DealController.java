package org.deal.controller;

import jakarta.validation.Valid;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/deal")
public class DealController {

    @PostMapping("/statement")
    ResponseEntity<Object> getOffers(@Valid @RequestBody LoanStatementRequestDto dto){
        return ResponseEntity.ok(new LoanOfferDto()); //заглушка
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

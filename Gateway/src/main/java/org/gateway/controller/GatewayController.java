package org.gateway.controller;

import org.gateway.dto.LoanOfferDto;
import org.gateway.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/gateway")
public class GatewayController {


    @PostMapping("/deal/statement")
    ResponseEntity<Object> dealStatementAPI(@RequestBody LoanStatementRequestDto loanStatementRequestDto){

        return null; //пока заглушка
    }

    @PostMapping("/deal/offer/select")
    ResponseEntity<Object> dealOfferSelectAPI(@RequestBody LoanOfferDto loanOfferDto){

        return null; //пока заглушка
    }

    @PostMapping("/deal/calculate")
    ResponseEntity<Object> dealOfferSelectAPI(@RequestBody LoanStatementRequestDto loanStatementRequestDto){

        return null; //пока заглушка
    }

    @PostMapping("/statement/statement")
    ResponseEntity<Object> statementStatementAPI(@PathVariable("statementId") UUID statementID){

        return null; //пока заглушка
    }

    @PostMapping("/statement/offer")
    ResponseEntity<Object> statementOfferAPI(@RequestBody LoanOfferDto loanOfferDto){

        return null; //пока заглушка
    }
}

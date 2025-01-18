package org.gateway.controller;

import org.gateway.dto.FinishRegistrationRequestDto;
import org.gateway.dto.LoanOfferDto;
import org.gateway.dto.LoanStatementRequestDto;
import org.gateway.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final RestService restService;

    @Autowired
    public GatewayController(RestService restService) {
        this.restService = restService;
    }


    // deal/statement и /deal/offer/select здесь нет, т.к. они вызываются через statement


    @PostMapping("/deal/calculate{statementId}")
    ResponseEntity<String> dealCalculateAPI(@RequestParam("statementId") UUID statementId,
                                                    @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto){
        return restService.sendRequestToDealCalculate(statementId, finishRegistrationRequestDto);
    }


    @PostMapping("/statement/statement")
    List<LoanOfferDto> statementStatementAPI(@RequestBody LoanStatementRequestDto loanStatementRequestDto){
        return restService.sendRequestToStatementStatement(loanStatementRequestDto);

    }

    @PostMapping("/statement/offer")
    ResponseEntity statementOfferAPI(@RequestBody LoanOfferDto loanOfferDto){
        return restService.sendRequestToStatementOffer(loanOfferDto);

    }
}

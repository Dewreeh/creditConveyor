package org.gateway.controller;

import org.gateway.dto.FinishRegistrationRequestDto;
import org.gateway.dto.LoanOfferDto;
import org.gateway.dto.LoanStatementRequestDto;
import org.gateway.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

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
        try {
            return restService.sendRequestToDealCalculate(statementId, finishRegistrationRequestDto);
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Скоринг не пройден");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка на сервере");
        }
    }


    @PostMapping("/statement/statement")
    ResponseEntity<Object> statementStatementAPI(@RequestBody LoanStatementRequestDto loanStatementRequestDto){
        try {
            List<LoanOfferDto> response = restService.sendRequestToStatementStatement(loanStatementRequestDto);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка на сервере: " + e.getMessage());
        }

    }

    @PostMapping("/statement/offer")
    ResponseEntity<String> statementOfferAPI(@RequestBody LoanOfferDto loanOfferDto){
        try {
            return restService.sendRequestToStatementOffer(loanOfferDto);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/send")
    ResponseEntity<String> dealDocumentSendApi(@PathVariable("statementId") UUID statementId){
        try {
            return restService.sendRequestToDealDocumentSend(statementId);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/sign")
    ResponseEntity<String> dealDocumentSignApi(@PathVariable("statementId") UUID statementId){
        try {
            return restService.sendRequestToDealDocumentSign(statementId);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/{code}")
    ResponseEntity<String> dealDocumentSignApi(@PathVariable("statementId") UUID statementId,
                                               @PathVariable("code") UUID ses_code){
        try {
            return restService.sendRequestToDealDocumentCode(statementId, ses_code);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

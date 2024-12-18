package org.deal.controller;

import jakarta.validation.Valid;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.ScoringDataDto;
import org.deal.model.Client;
import org.deal.service.ClientService;
import org.deal.service.FinishRegistartionService;
import org.deal.service.SelectService;
import org.deal.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
public class DealController {
    private final StatementService statementService;
    private final SelectService selectService;
    private final ClientService clientService;
    private final FinishRegistartionService finishRegistartionService;

    @Autowired
    public DealController(StatementService statementService,
                          SelectService selectService,
                          FinishRegistartionService finishRegistartionService,
                          ClientService clientService) {

        this.statementService = statementService;
        this.selectService = selectService;
        this.finishRegistartionService = finishRegistartionService;
        this.clientService = clientService;
    }

    @PostMapping("/statement")
    ResponseEntity<Object> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        List<LoanOfferDto> offers;
        try {
            // Получаем офферы
            offers = statementService.getOffers(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        try {
            // Сохраняем клиента если офферы получены (прескоринг прошел)
            Client client = clientService.saveClient(dto);
            UUID statementUuid = statementService.saveStatement(client);
            offers = statementService.setUuidForOffers(offers, statementUuid);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
        return ResponseEntity.ok(offers);
    }
    @PostMapping("/offer/select")
    ResponseEntity<Object> selectOffer(@Valid @RequestBody LoanOfferDto dto){
        try {
            selectService.applyOffer(dto);
            return ResponseEntity.ok("Успех");
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
    @PostMapping("/offer/calculate")
    ResponseEntity<Object> calculate(@Valid @RequestBody FinishRegistrationRequestDto dto,
                                     @RequestParam("statementId") UUID statementId){
        try {
            finishRegistartionService.finishRegistration(dto, statementId);
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
        return ResponseEntity.ok("Кредит посчитан!");
    }


}

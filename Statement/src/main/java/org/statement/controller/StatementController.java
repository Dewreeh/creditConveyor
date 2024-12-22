package org.statement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.statement.service.PrescoringService;
import org.statement.service.RestQueriesService;
import org.statement.dto.LoanStatementRequestDto;

@RestController
@RequestMapping("/statement")
public class StatementController {

    private final PrescoringService prescoringService;
    private final RestQueriesService restQueriesService;

    @Autowired
    public StatementController(PrescoringService prescoringService,
                               RestQueriesService restQueriesService){

        this.prescoringService = prescoringService;
        this.restQueriesService = restQueriesService;
    }

    @PostMapping("/")
    ResponseEntity<Object> statementHandler(@RequestBody LoanStatementRequestDto dto){
        if(prescoringService.doPrescoring(dto)){
            return ResponseEntity
                    .ok(restQueriesService.getOffersFromDeal(dto));
        } else{
            return ResponseEntity.unprocessableEntity().body("Данные не прошли прескоринг, перепроверьте их и попробуйте снова");
        }

    }
}

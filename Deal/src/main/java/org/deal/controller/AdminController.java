package org.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.deal.model.Statement;
import org.deal.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/deal/admin")
public class AdminController {

    StatementService statementService;

    @Autowired
    AdminController(StatementService statementService){
        this.statementService = statementService;
    }
    @GetMapping("/statement/{statementId}")
    @Operation(
            summary = "Получение заявки по её ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Содержимое заявки", content = @Content(schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            }
    )
    ResponseEntity<Object> getStatement(@PathVariable("statementId") UUID statementID){
        log.info("Запрос на получение заявки по ID: {}", statementID);
        try{
            Statement statement = statementService.getStatement(statementID);
            log.info("Успешно получена заявка {}", statementID);
            return ResponseEntity.ok().body(statement);
        } catch (Exception e){
            log.info("Заявка не найдена {}", statementID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/statement")
    @Operation(
            summary = "Получение всех заявок по её ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок", content = @Content(schema = @Schema(implementation = Statement[].class))),
                    @ApiResponse(responseCode = "404", description = "Ошибка при получении заявок"),
            }
    )
    ResponseEntity<Object> getAllStatements(){
        log.info("Запрос на получение всех заявок");
        try{
            List<Statement> statementList = statementService.getAllStatements();
            log.info("Найдено {} заявок", statementList.size());
            return ResponseEntity.ok().body(statementList);
        } catch (Exception e){
            log.info("Ошибка выгрузки заявок");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

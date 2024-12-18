package org.deal.service;

import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.ChangeType;
import org.deal.model.Client;
import org.deal.model.Statement;
import org.deal.repository.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StatementService {

    private final StatementRepository statementRepository;

    @Autowired
    public StatementService(StatementRepository statementRepository, ClientService clientService) {
        this.statementRepository = statementRepository;
    }

    //получаем офферы через МС calculator
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        RestClient restClient = RestClient.builder().build();
        try {
            return restClient.post()
                    .uri("http://localhost:8080/calculator/offers")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Данные не прошли прескоринг. Пожалуйста, перепроверьте их и отправьте новый запрос", e);
        }
    }

    // Сохранение заявки
    @Transactional
    public UUID saveStatement(Client client) {
        Statement statement = new Statement();
        UUID uuid = UUID.randomUUID();
        statement.setStatementId(uuid);
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statement.setCreationDate(LocalDateTime.now());

        // Создаем объект статуса для истории
        StatementStatusHistoryDto statusHistoryDto = createStatusHistory(ApplicationStatus.DOCUMENT_CREATED, ChangeType.AUTOMATIC);
        statement.setStatusHistory(List.of(statusHistoryDto));

        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(null);
        loanOfferDto.setRequestAmount(null);
        loanOfferDto.setTotalAmount(null);
        loanOfferDto.setTerm(null);
        loanOfferDto.setMonthlyPayment(null);
        loanOfferDto.setRate(null);
        loanOfferDto.setIsInsuranceEnabled(null);
        loanOfferDto.setIsSalaryClient(null);

        statement.setAppliedOffer(loanOfferDto);

        // Сохраняем заявку
        statementRepository.save(statement);

        return uuid;
    }

    // Устанавливаем UUID для офферов
    public List<LoanOfferDto> setUuidForOffers(List<LoanOfferDto> dto, UUID StatementUuid) {
        for (LoanOfferDto item : dto) {
            item.setStatementId(StatementUuid);
        }
        return dto;
    }

    // Получение заявки по UUID
    public Statement getStatement(UUID statementUuid) {
        Statement statement = statementRepository.getByStatementId(statementUuid);
        if (statement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка с таким ID не найдена");
        }
        return statement;
    }

    // Создание истории статуса заявки
    private StatementStatusHistoryDto createStatusHistory(ApplicationStatus status, ChangeType changeType) {
        StatementStatusHistoryDto statusHistoryDto = new StatementStatusHistoryDto();
        statusHistoryDto.setStatus(String.valueOf(status));
        statusHistoryDto.setTime(new java.util.Date());
        statusHistoryDto.setChangeType(changeType);
        return statusHistoryDto;
    }
}

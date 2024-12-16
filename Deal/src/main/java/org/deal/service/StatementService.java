package org.deal.service;

import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.ChangeType;
import org.deal.model.Client;
import org.deal.model.Statement;
import org.deal.repository.ClientRepository;
import org.deal.repository.LoanOfferAttributeConverter;
import org.deal.repository.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;

import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StatementService {


    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    @Autowired //в предыдущем МС инжектил бины через поля, узнал, что так не рекомендуется и сейчас инжекчу в конструктор :)
    public StatementService(ClientRepository clientRepository,
                            StatementRepository statementRepository) {
        this.clientRepository = clientRepository;
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

    public Client saveClient(LoanStatementRequestDto dto){
        Client client = createClientEntity(dto);
        try {
            clientRepository.save(client);
        } catch(DataIntegrityViolationException e){
            //сгенеренный только что id не совпадёт с id какого-либо клиента,
            //но почта уже может быть в бд
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Клиент уже существует", e);
        }
        return client;
    }

    public UUID saveStatement(Client client) {
        Statement statement = new Statement();
        UUID uuid = UUID.randomUUID();
        statement.setStatementId(uuid);
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statement.setCreationDate(LocalDateTime.now());

        // Создаем объект статуса для истории
        StatementStatusHistoryDto statusHistoryDto = createStatusHistory(ApplicationStatus.DOCUMENT_CREATED, ChangeType.AUTOMATIC);

        // Добавляем статус в историю
        statement.setStatusHistory(List.of(statusHistoryDto));  // История статусов на момент создания заявки

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

    public List<LoanOfferDto> setUuidForOffers(List<LoanOfferDto> dto, UUID StatementUuid){
        for(LoanOfferDto item: dto){
            item.setStatementId(StatementUuid);
        }
        return dto;
    }



    private Client createClientEntity(LoanStatementRequestDto dto) {
        //Заполняем инфу, которую имеем в LoanStatementRequestDto (а там не всё :( )
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setEmail(dto.getEmail());
        client.setBirthDate(dto.getBirthdate());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());
        return client;
    }
    private StatementStatusHistoryDto createStatusHistory(ApplicationStatus status, ChangeType changeType) {
        StatementStatusHistoryDto statusHistoryDto = new StatementStatusHistoryDto();
        statusHistoryDto.setStatus(String.valueOf(status));
        statusHistoryDto.setTime(new Date());
        statusHistoryDto.setChangeType(changeType);
        return statusHistoryDto;
    }


}

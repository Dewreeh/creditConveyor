package org.deal.service;

import jakarta.transaction.Transactional;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.ChangeType;
import org.deal.model.Client;
import org.deal.model.Passport;
import org.deal.model.Statement;
import org.deal.repository.ClientRepository;
import org.deal.repository.LoanOfferAttributeConverter;
import org.deal.repository.PassportRepository;
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
    private final PassportRepository passportRepository;

    @Autowired //в предыдущем МС инжектил бины через поля, узнал, что так не рекомендуется и сейчас инжекчу в конструктор :)
    public StatementService(ClientRepository clientRepository,
                            StatementRepository statementRepository,
                            PassportRepository passportRepository) {
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.passportRepository = passportRepository;
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
        Passport passport = savePassport(dto);
        Client client = createClientEntity(dto, passport);
        try {
            clientRepository.save(client);
        } catch(DataIntegrityViolationException e){
            //сгенеренный только что id не совпадёт с id какого-либо клиента,
            //но почта уже может быть в бд
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Клиент уже существует", e);
        }
        return client;
    }
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
    public Statement getStatement(UUID statementUuid){
        Statement statement = statementRepository.getByStatementId(statementUuid);
        if (statement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка с таким ID не найдена");
        }
        return statement;
    }

    private Client createClientEntity(LoanStatementRequestDto dto, Passport passport) {
        //Заполняем инфу, которую имеем в LoanStatementRequestDto (а там не всё :( )
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setEmail(dto.getEmail());
        client.setBirthDate(dto.getBirthdate());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());
        client.setPassport(passport);
        return client;
    }
    @Transactional
    public Passport savePassport(LoanStatementRequestDto dto) {
        Passport passport = createPassportEntity(dto);
        try {
            passportRepository.save(passport);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Паспорт уже существует", e);
        }
        return passport;
    }

    private Passport createPassportEntity(LoanStatementRequestDto dto) {
        //сохраняем, то что есть в LoanStatementRequestDto
        Passport passport = new Passport();
        passport.setPassportUuid(UUID.randomUUID());
        passport.setSeries(dto.getPassportSeries());
        passport.setNumber(dto.getPassportNumber());
        return passport;
    }
    private StatementStatusHistoryDto createStatusHistory(ApplicationStatus status, ChangeType changeType) {
        StatementStatusHistoryDto statusHistoryDto = new StatementStatusHistoryDto();
        statusHistoryDto.setStatus(String.valueOf(status));
        statusHistoryDto.setTime(new Date());
        statusHistoryDto.setChangeType(changeType);
        return statusHistoryDto;
    }


}

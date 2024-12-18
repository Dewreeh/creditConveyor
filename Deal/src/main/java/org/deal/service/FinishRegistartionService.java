package org.deal.service;

import jakarta.transaction.Transactional;
import org.deal.dto.CreditDto;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.ScoringDataDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.CreditStatus;
import org.deal.model.Client;
import org.deal.model.Credit;
import org.deal.model.Statement;
import org.deal.repository.ClientRepository;
import org.deal.repository.CreditRepository;
import org.deal.repository.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FinishRegistartionService {

    private final CreditRepository creditRepository;
    private final StatementRepository statementRepository;
    private final StatementService statementService;

    @Autowired
    public FinishRegistartionService(StatementService statementService,
                                     CreditRepository creditRepository,
                                     StatementRepository statementRepository) {
        this.creditRepository = creditRepository;
        this.statementRepository = statementRepository;
        this.statementService = statementService;

    }
    @Transactional
    public void finishRegistration(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID statementUuid){
        Statement statement = statementRepository.getByStatementId(statementUuid);
        if (statement == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка с таким ID не найдена");

        Client client = statement.getClient(); //достаем клиента из statement
        LoanOfferDto loanOfferDto = statement.getAppliedOffer(); //достаём оффер который клиент ранее принял
        ScoringDataDto scoringDataDto = getScoringDataDto(finishRegistrationRequestDto, client, loanOfferDto); //заполняем ScoringDataDto
        CreditDto creditDto = getCreditFromCalculator(scoringDataDto); //запрос на МС калькулятор
        Credit credit = saveCredit(creditDto);

        //обновляем состояние заявки
        statement.setCredit(credit);
        statement.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        statement.setSignDate(LocalDateTime.now());
        statementRepository.save(statement);
    }

     private ScoringDataDto getScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto, Client client, LoanOfferDto loanOfferDto){
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(loanOfferDto.getTotalAmount());
        scoringDataDto.setTerm(loanOfferDto.getTerm());
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());
        scoringDataDto.setGender(finishRegistrationRequestDto.getGender());
        scoringDataDto.setBirthdate(client.getBirthDate());
        scoringDataDto.setPassportIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
        scoringDataDto.setPassportIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch());
        scoringDataDto.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
        scoringDataDto.setDependentAmount(finishRegistrationRequestDto.getDependentAmount());
        scoringDataDto.setEmployment(finishRegistrationRequestDto.getEmployment());
        scoringDataDto.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());
        scoringDataDto.setIsInsuranceEnabled(loanOfferDto.getIsInsuranceEnabled());
        scoringDataDto.setIsSalaryClient(loanOfferDto.getIsSalaryClient());
        return scoringDataDto;
    }
    public Credit saveCredit(CreditDto creditDto){
        Credit credit = new Credit();
        credit.setCreditId(UUID.randomUUID());
        credit.setAmount(creditDto.getAmount());
        credit.setTerm(creditDto.getTerm());
        credit.setPsk(creditDto.getPsk());
        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setPaymentSchedule(creditDto.getPaymentSchedule());
        credit.setRate(creditDto.getRate());
        credit.setInsuranceEnabled(creditDto.getIsInsuranceEnabled());
        credit.setCreditStatus(CreditStatus.CALCULATED);
        return creditRepository.save(credit);
    }

    private CreditDto getCreditFromCalculator(ScoringDataDto dto){
        RestClient restClient = RestClient.builder().build();
        try {
            return restClient.post()
                    .uri("http://localhost:8080/calculator/calc")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(dto)
                    .retrieve()
                    .body(CreditDto.class);
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Данные не прошли скоринг. (Прилетает с МС калькулятор)", e);
        }
    }



}

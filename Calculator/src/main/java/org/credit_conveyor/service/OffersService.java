package org.credit_conveyor.service;


import lombok.Getter;
import lombok.Setter;
import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OffersService {
    @Value("${application.baseRate}") @Getter @Setter //геттер и сеттер для тестов
    private BigDecimal baseRate;

    @Value("${application.insuranceCost}") @Getter @Setter //решил захардкодить стоимость страховки по аналогии со ставкой (про которую написано в задании)
    private BigDecimal insuranceCost;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto){

        List<LoanOfferDto> loanOffers = new ArrayList<>();

        UUID uuid = UUID.randomUUID(); //генерируем общий uuid для будущих офферов
        //Перебираем все комбинации полей isInsuranceEnabled и isSalaryClient
        for(Boolean isInsuranceEnabled: Arrays.asList(true, false)){
            for(Boolean isSalaryClient: Arrays.asList(true, false)){
                loanOffers.add(createOffer(loanStatementRequestDto, isInsuranceEnabled, isSalaryClient, uuid));
            }
        }
        
        return loanOffers.stream()
                .sorted( (LoanOfferDto o1, LoanOfferDto o2) -> o2.getRate().subtract(o1.getRate()).intValue())
                .collect(Collectors.toList());
    }


    private LoanOfferDto createOffer(LoanStatementRequestDto loanStatementRequestDto,
                                     Boolean isInsuranceEnabled,
                                     Boolean isSalaryClient,
                                     UUID uuid){


        BigDecimal requestAmount = loanStatementRequestDto.getAmount(); //получаем сумму запроса из заявки
        
        Integer requestTerm = loanStatementRequestDto.getTerm(); //получаем срок кредита

        LoanOfferDto loanOffer = new LoanOfferDto();

        //устанавливаем те значения, которые нам уже известны
        loanOffer.setStatementId(uuid);

        loanOffer.setRequestAmount(requestAmount);

        loanOffer.setIsInsuranceEnabled(isInsuranceEnabled);

        loanOffer.setIsSalaryClient(isSalaryClient);

        loanOffer.setTerm(requestTerm);

        //значения которые зависят от isSalaryClient и isInsuranceEnabled

        //сначала ставим базовые значения
        loanOffer.setTotalAmount(requestAmount);
        loanOffer.setRate(baseRate);

        //изменяем их (или не изменяем) в зависимости от условий
        if(isInsuranceEnabled){
            loanOffer.setTotalAmount(requestAmount.add(insuranceCost)); //добавляем в тело стоимость страховки
            loanOffer.setRate(baseRate.subtract(new BigDecimal(3))); //вычитаем из ставки 3%
        }
        if(isSalaryClient){
            BigDecimal currentRate = loanOffer.getRate(); //получаем значение которое лежит после предыдущего условия (там либо baseRate, либо baseRate-3)
            loanOffer.setRate(currentRate.subtract(new BigDecimal(1))); //вычитаем из него 1
        }

        calculateMonthlyPayment(loanOffer, requestTerm);

        return loanOffer;
    }

    private void calculateMonthlyPayment(LoanOfferDto loanOfferDto, Integer requestTerm){
        BigDecimal monthlyPayment = loanOfferDto.getTotalAmount().divide(new BigDecimal(requestTerm), 2,  RoundingMode.HALF_UP);
        loanOfferDto.setMonthlyPayment(monthlyPayment);

    }

    public Boolean isValid(LoanStatementRequestDto dto){
        boolean isValid = true;

        if(dto.getFirstName() == null || dto.getFirstName().length() < 2 || dto.getFirstName().length() > 30){
            isValid = false;
        }
        if(dto.getLastName() == null || dto.getLastName().length() < 2 || dto.getLastName().length() > 30){
            isValid = false;
        }
        if(dto.getMiddleName() == null || dto.getMiddleName().length() < 2 || dto.getMiddleName().length() > 30){
            isValid = false;
        }
        if(dto.getAmount() == null || dto.getAmount().compareTo(new BigDecimal("20000")) < 0){
            isValid = false;
        }
        if(dto.getTerm() == null || dto.getTerm() < 6){
            isValid = false;
        }
        if (dto.getEmail() == null || !dto.getEmail().matches("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")){
            isValid = false;
        }

        if(dto.getPassportSeries() == null || !dto.getPassportSeries().matches("\\d{4}")){
            isValid = false;
        }
        if(dto.getPassportNumber() == null || !dto.getPassportNumber().matches("\\d{6}")){
            isValid = false;
        }

        LocalDate minDate = LocalDate.now().minusYears(18);
        if(dto.getBirthdate() == null || minDate.isBefore(dto.getBirthdate())){
            isValid = false;
        }
        return isValid;
    }



}

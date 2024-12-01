package org.credit_conveyor.service;


import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OffersService {
    @Value("${application.baseRate}")
    private BigDecimal baseRate;

    @Value("${application.insuranceCost}") //решил захардкодить стоимость страховки по аналогии со ставкой (про которую написано в задании)
    private BigDecimal insuranceCost;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto){

        List<LoanOfferDto> loanOffers = new ArrayList<>();

        //Перебираем все комбинации полей isInsuranceEnabled и isSalaryClient
        for(Boolean isInsuranceEnabled: Arrays.asList(true, false)){
            for(Boolean isSalaryClient: Arrays.asList(true, false)){
                loanOffers.add(createOffer(loanStatementRequestDto, isInsuranceEnabled, isSalaryClient));
            }
        }
        
        return loanOffers.stream()
                .sorted((LoanOfferDto o1, LoanOfferDto o2) -> o2.getRate().subtract(o1.getRate()).intValue())
                .collect(Collectors.toList());
    }


    private LoanOfferDto createOffer(LoanStatementRequestDto loanStatementRequestDto,
                                     Boolean isInsuranceEnabled,
                                     Boolean isSalaryClient){

        UUID uuid = UUID.randomUUID();

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
            loanOffer.setRate(baseRate.subtract(BigDecimal.valueOf(3))); //вычитаем из ставки 3%
        }
        if(isSalaryClient){
            BigDecimal currentRate = loanOffer.getRate(); //получаем значение которое лежит после предыдущего условия (там либо baseRate, либо baseRate-3)
            loanOffer.setRate(currentRate.subtract(BigDecimal.valueOf(1))); //вычитаем из него 1
        }

        calculateMonthlyPayment(loanOffer, requestTerm);

        return loanOffer;
    }

    private void calculateMonthlyPayment(LoanOfferDto loanOfferDto, Integer requestTerm){
        BigDecimal monthlyPayment = loanOfferDto.getTotalAmount().divide(BigDecimal.valueOf(requestTerm), 2,  RoundingMode.HALF_UP);
        loanOfferDto.setMonthlyPayment(monthlyPayment);

    }


}

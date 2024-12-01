package org.credit_conveyor.service;


import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final List<LoanOfferDto> loanOffers = new ArrayList<>();
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto){

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

        LoanOfferDto loanOfferDto = new LoanOfferDto();

        //устанавливаем те значения, которые нам уже известны
        loanOfferDto.setStatementId(uuid);

        loanOfferDto.setRequestAmount(requestAmount);

        loanOfferDto.setIsInsuranceEnabled(isInsuranceEnabled);

        loanOfferDto.setIsSalaryClient(isSalaryClient);

        loanOfferDto.setTerm(requestTerm);

        //значения которые зависят от isSalaryClient и isInsuranceEnabled

        //сначала ставим базовые значения
        loanOfferDto.setTotalAmount(requestAmount);
        loanOfferDto.setRate(baseRate);

        //изменяем их (или не изменяем) в зависимости от условий
        if(isInsuranceEnabled){
            loanOfferDto.setTotalAmount(requestAmount.add(insuranceCost)); //добавляем в тело стоимость страховки
            loanOfferDto.setRate(baseRate.subtract(BigDecimal.valueOf(3))); //вычитаем из ставки 3%
        }
        if(isSalaryClient){
            BigDecimal currentRate = loanOfferDto.getRate(); //получаем значение которое лежит после предыдущего условия (там либо baseRate, либо baseRate-3)
            loanOfferDto.setRate(currentRate.subtract(BigDecimal.valueOf(1))); //вычитаем из него 1
        }
        //считаем ежемесячный платёж, поделив общую сумму кредита на срок из запроса на кредит
        BigDecimal monthlyPayment = loanOfferDto.getTotalAmount().divide(BigDecimal.valueOf(requestTerm), 2,  RoundingMode.HALF_UP);

        loanOfferDto.setMonthlyPayment(monthlyPayment);

        return loanOfferDto;
    }


}

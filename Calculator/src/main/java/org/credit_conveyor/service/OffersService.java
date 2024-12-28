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
        int term = loanOfferDto.getTerm();
        BigDecimal rate = loanOfferDto.getRate();
        BigDecimal amount = loanOfferDto.getTotalAmount();

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP); //ставка за месяц (ставка поделенная на 12 и на 100)

        // Числитель
        BigDecimal numerator = monthlyRate.multiply(BigDecimal.ONE.add(monthlyRate).pow(term));

        // Знаменатель
        BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE);

        // Аннуитетный коэффициент
        BigDecimal annuityFactor = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

        // ежемесячный платёж
        BigDecimal monthlyPayment = amount.multiply(annuityFactor).setScale(2, RoundingMode.HALF_UP);
        loanOfferDto.setMonthlyPayment(monthlyPayment);

    }




}

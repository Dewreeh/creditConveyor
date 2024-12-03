package org.credit_conveyor.service;

import lombok.Getter;
import lombok.Setter;
import org.credit_conveyor.dto.CreditDto;
import org.credit_conveyor.dto.PaymentScheduleElementDto;
import org.credit_conveyor.dto.ScoringDataDto;
import org.credit_conveyor.enums.EmploymentStatus;
import org.credit_conveyor.enums.Gender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.credit_conveyor.enums.EmploymentStatus.OWNER;

@Service
public class CalcService {
    @Value("${application.baseRate}") @Getter @Setter //геттер и сеттер для тестов
    private BigDecimal baseRate;

    @Value("${application.insuranceCost}") @Getter @Setter //геттер и сеттер для тестов
    private BigDecimal insuranceCost;
    CreditDto credit = new CreditDto();
    public CreditDto getCredit(ScoringDataDto scoringDataDto){
        CreditDto credit = new CreditDto();

        credit.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        credit.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());

        //если страховка включена, добавляем её в тело кредита
        if (scoringDataDto.getIsInsuranceEnabled() != null && scoringDataDto.getIsInsuranceEnabled()) {
            credit.setAmount(scoringDataDto.getAmount().add(insuranceCost));
        } else{
            credit.setAmount(scoringDataDto.getAmount());
        }
        credit.setRate(calculateRate(scoringDataDto));
        credit.setTerm(scoringDataDto.getTerm());
        credit.setMonthlyPayment(calculateMonthlyPayment(scoringDataDto, credit.getRate(), credit.getAmount()));
        credit.setPsk(calculatePsk(scoringDataDto,credit.getRate(), credit.getAmount()));
        credit.setPaymentSchedule(getPaymentSchedule(scoringDataDto, credit));


        return credit;
    }
    //Здесь обрабатываются все случаи, при которых происходит отказ
    public boolean isScoringDataOk(ScoringDataDto dto){

        boolean isOk = true;
        if(dto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED){
            isOk = false;
        }
        if(dto.getEmployment().getSalary().multiply(new BigDecimal(24)).compareTo(dto.getAmount()) < 0){
            isOk = false;
        }
        if(dto.getEmployment().getWorkExperienceTotal() < 18 || dto.getEmployment().getWorkExperienceCurrent() < 3){
            isOk = false;
        }
        if(getAge(dto) < 18 || getAge(dto) > 60){
            isOk = false;
        }
        return isOk;
    }

    private int getAge(ScoringDataDto dto){
        Period period = Period.between(dto.getBirthdate(), LocalDate.now());
        return period.getYears();
    }

    //Высчитывает итоговую ставку (проверка на условия для отказа вынесена в отдельный метод)
    private BigDecimal calculateRate(ScoringDataDto dto){

        BigDecimal finalRate = baseRate; //берём за основу базовую ставку

        //Условие по статусу трудоустройства (unemployed получают отказ в isScoringDataOk)
        switch(dto.getEmployment().getEmploymentStatus()){
            case SELF_EMPLOYED: finalRate = finalRate.add(new BigDecimal(2));
                break;
            case OWNER: finalRate = finalRate.add(new BigDecimal(1));
                break;
        }
        //по должности
        switch(dto.getEmployment().getPosition()){
            case TOP_MANAGER, DIRECTOR: finalRate = finalRate.subtract(new BigDecimal(3));
                break;
            case MANAGER, HR: finalRate = finalRate.subtract(new BigDecimal(2));
                break;
            case DEVELOPER: finalRate = finalRate.subtract(new BigDecimal(5));
                break;
        }
        //по личному фронту
        switch(dto.getMaritalStatus()){
            case MARRIED: finalRate = finalRate.subtract(new BigDecimal(3));
                break;
            case SINGLE: finalRate = finalRate.add(new BigDecimal("0, 5"));
                break;
            case DIVORCED: finalRate = finalRate.add(new BigDecimal(1));
                break;
        }

        //все проверки, связанные с гендером и возрастом
        Gender gender = dto.getGender();
        int age = getAge(dto);
        if(gender == Gender.FEMALE && age > 32 || age < 60){
            finalRate = finalRate.subtract(new BigDecimal(3));
        }
        if(gender == Gender.MALE && age> 30 || age < 50){
            finalRate = finalRate.subtract(new BigDecimal(3));
        }
        if(gender == Gender.NON_BINARY){
            finalRate = finalRate.add(new BigDecimal(8));
        }
        if(gender == Gender.CAT){
            finalRate = finalRate.subtract(new BigDecimal(1));
        }
        return finalRate;
    }

    private BigDecimal calculatePsk(ScoringDataDto dto, BigDecimal rate, BigDecimal insuranceAmount) {
        // Получаем ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(dto, rate, insuranceAmount);

        // Общая сумма выплат без страховки
        BigDecimal totalToPay = monthlyPayment.multiply(BigDecimal.valueOf(dto.getTerm()));

        //по формуле отсюда https://www.raiffeisen.ru/wiki/chto-takoe-polnaya-stoimost-kredita/
        BigDecimal psk = totalToPay.divide(dto.getAmount(), 10, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(dto.getTerm()), 10, RoundingMode.HALF_UP);

        BigDecimal effectivePsk = BigDecimal.ONE.add(psk.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .pow(12)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100));
        return effectivePsk.setScale(2, RoundingMode.HALF_UP);
    }

    //считаем ежемесячную выплату
    private BigDecimal calculateMonthlyPayment(ScoringDataDto dto, BigDecimal rate, BigDecimal amountWithInsurance){

        int term = dto.getTerm();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP); //ставка за месяц (ставка поделенная на 12 и на 100)

        // Числитель
        BigDecimal numerator = monthlyRate.multiply(BigDecimal.ONE.add(monthlyRate).pow(term));

        // Знаменатель
        BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE);

        // Аннуитетный коэффициент
        BigDecimal annuityFactor = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

        // ежемесячный платёж
        return amountWithInsurance.multiply(annuityFactor).setScale(2, RoundingMode.HALF_UP);

    }

    private List<PaymentScheduleElementDto> getPaymentSchedule(ScoringDataDto scoringDataDto, CreditDto creditDto){
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();


        LocalDate firstPaymentDate = LocalDate.now().plusMonths(1);

        BigDecimal monthlyPayment = calculateMonthlyPayment(scoringDataDto, creditDto.getRate(), creditDto.getAmount());
        BigDecimal remainingDebt = scoringDataDto.getAmount(); //начальный долг

        for(int number = 1; number <= scoringDataDto.getTerm(); number++){
            PaymentScheduleElementDto scheduleElement = new PaymentScheduleElementDto();
            scheduleElement.setNumber(number);
            scheduleElement.setDate(firstPaymentDate.plusMonths(number - 1));

            BigDecimal interestPayment = remainingDebt.multiply(creditDto.getRate().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment).max(BigDecimal.valueOf(0));

            scheduleElement.setTotalPayment(monthlyPayment);
            scheduleElement.setInterestPayment(interestPayment);
            scheduleElement.setDebtPayment(debtPayment);
            scheduleElement.setRemainingDebt(remainingDebt);
            schedule.add(scheduleElement);
        }
        return schedule;
    }

}

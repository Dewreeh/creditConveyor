package org.credit_conveyor.service;

import org.credit_conveyor.dto.CreditDto;
import org.credit_conveyor.dto.ScoringDataDto;
import org.credit_conveyor.dto.EmploymentDto;
import org.credit_conveyor.enums.EmploymentStatus;
import org.credit_conveyor.enums.Gender;
import org.credit_conveyor.enums.MaritalStatus;
import org.credit_conveyor.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CalcServiceTest {

    private CalcService calcService;

    @Mock
    private ScoringDataDto scoringDataDto;
    @Mock
    private EmploymentDto employmentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calcService = new CalcService();
    }

    @Test
    void testGetCredit() {
        when(scoringDataDto.getAmount()).thenReturn(new BigDecimal("100000"));
        when(scoringDataDto.getTerm()).thenReturn(12);
        when(scoringDataDto.getIsInsuranceEnabled()).thenReturn(true);
        when(scoringDataDto.getIsSalaryClient()).thenReturn(true);
        when(scoringDataDto.getGender()).thenReturn(Gender.FEMALE);
        when(scoringDataDto.getBirthdate()).thenReturn(LocalDate.of(1990, 5, 15));
        when(scoringDataDto.getPassportSeries()).thenReturn("1234");
        when(scoringDataDto.getPassportNumber()).thenReturn("567890");
        when(scoringDataDto.getPassportIssueDate()).thenReturn(LocalDate.of(2025, 6, 1));
        when(scoringDataDto.getPassportIssueBranch()).thenReturn("aaa");
        when(scoringDataDto.getMaritalStatus()).thenReturn(MaritalStatus.MARRIED);
        when(scoringDataDto.getDependentAmount()).thenReturn(0);
        when(scoringDataDto.getAccountNumber()).thenReturn("1234567890");
        when(scoringDataDto.getEmployment()).thenReturn(employmentDto);
        when(employmentDto.getEmploymentStatus()).thenReturn(EmploymentStatus.OWNER);
        when(employmentDto.getSalary()).thenReturn(new BigDecimal("50000"));
        when(employmentDto.getPosition()).thenReturn(Position.MANAGER);
        when(employmentDto.getWorkExperienceTotal()).thenReturn(5);
        when(employmentDto.getWorkExperienceCurrent()).thenReturn(2);

        calcService.setBaseRate(new BigDecimal("20.00"));
        calcService.setInsuranceCost(new BigDecimal("500"));

        CreditDto credit = calcService.getCredit(scoringDataDto);

        assertNotNull(credit);
        assertEquals(new BigDecimal("100500"), credit.getAmount());
        assertEquals(new BigDecimal("10.00"), credit.getRate());
        assertNotNull(credit.getPaymentSchedule());
    }

    @Test
    void testGetCreditWithoutInsurance() {
        when(scoringDataDto.getAmount()).thenReturn(new BigDecimal("100000"));
        when(scoringDataDto.getTerm()).thenReturn(12);
        when(scoringDataDto.getIsInsuranceEnabled()).thenReturn(false);
        when(scoringDataDto.getIsSalaryClient()).thenReturn(true);
        when(scoringDataDto.getGender()).thenReturn(Gender.FEMALE);
        when(scoringDataDto.getBirthdate()).thenReturn(LocalDate.of(1990, 5, 15));
        when(scoringDataDto.getPassportSeries()).thenReturn("1234");
        when(scoringDataDto.getPassportNumber()).thenReturn("567890");
        when(scoringDataDto.getPassportIssueDate()).thenReturn(LocalDate.of(2025, 6, 1));
        when(scoringDataDto.getPassportIssueBranch()).thenReturn("aaa");
        when(scoringDataDto.getMaritalStatus()).thenReturn(MaritalStatus.MARRIED);
        when(scoringDataDto.getDependentAmount()).thenReturn(0);
        when(scoringDataDto.getAccountNumber()).thenReturn("1234567890");
        when(scoringDataDto.getEmployment()).thenReturn(employmentDto);
        when(employmentDto.getEmploymentStatus()).thenReturn(EmploymentStatus.OWNER);
        when(employmentDto.getSalary()).thenReturn(new BigDecimal("50000"));
        when(employmentDto.getPosition()).thenReturn(Position.MANAGER);
        when(employmentDto.getWorkExperienceTotal()).thenReturn(5);
        when(employmentDto.getWorkExperienceCurrent()).thenReturn(2);

        calcService.setBaseRate(new BigDecimal("20.00"));
        calcService.setInsuranceCost(new BigDecimal("100000.00"));

        CreditDto credit = calcService.getCredit(scoringDataDto);

        assertNotNull(credit);
        assertEquals(new BigDecimal("100000"), credit.getAmount());
        assertEquals(new BigDecimal("10.00"), credit.getRate());
        assertNotNull(credit.getPaymentSchedule());
    }
}

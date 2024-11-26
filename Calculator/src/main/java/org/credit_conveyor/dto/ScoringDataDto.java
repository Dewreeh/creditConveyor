package org.credit_conveyor.dto;

import lombok.Data;
import org.credit_conveyor.enums.Gender;
import org.credit_conveyor.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class ScoringDataDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
package org.credit_conveyor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.credit_conveyor.enums.Gender;
import org.credit_conveyor.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class ScoringDataDto {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer term;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String middleName;
    private Gender gender;
    @NotNull
    private LocalDate birthdate;
    @NotNull
    private String passportSeries;
    @NotNull
    private String passportNumber;
    @NotNull
    private LocalDate passportIssueDate;
    @NotNull
    private String passportIssueBranch;
    @NotNull
    private MaritalStatus maritalStatus;
    @NotNull
    private Integer dependentAmount;
    @NotNull
    private EmploymentDto employment;
    @NotNull
    private String accountNumber;
    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;
}

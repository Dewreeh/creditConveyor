package org.gateway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.gateway.enums.Gender;
import org.gateway.enums.MaritalStatus;

import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {
    @NotNull
    Gender gender;
    @NotNull
    MaritalStatus maritalStatus;
    @NotNull
    Integer dependentAmount;
    @NotNull
    LocalDate passportIssueDate;
    @NotNull
    String PassportIssueBranch;
    @NotNull
    EmploymentDto employment;
    @NotNull
    String accountNumber;


}

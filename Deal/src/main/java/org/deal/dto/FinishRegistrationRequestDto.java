package org.deal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.deal.enums.Gender;
import org.deal.enums.MaritalStatus;

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

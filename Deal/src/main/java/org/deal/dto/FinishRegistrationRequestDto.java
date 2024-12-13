package org.deal.dto;

import org.deal.enums.Gender;
import org.deal.enums.MaritalStatus;

import java.time.LocalDate;

public class FinishRegistrationRequestDto {
    Gender gender;
    MaritalStatus maritalStatus;
    Integer dependentAmount;
    LocalDate passportIssueDate;
    String PassportIssueBranch;
    EmploymentDto employment;
    String accountNumber;


}

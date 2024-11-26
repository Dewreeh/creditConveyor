package org.example.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class LoanStatementRequestDto {

    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;

}

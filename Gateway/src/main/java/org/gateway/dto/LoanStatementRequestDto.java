package org.gateway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer term;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String middleName;
    @NotNull
    private String email;
    @NotNull
    private LocalDate birthdate;
    @NotNull
    private String passportSeries;
    @NotNull
    private String passportNumber;

}
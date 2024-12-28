package org.statement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoanOfferDto {
    @NotNull
    private UUID statementId;
    @NotNull
    private BigDecimal requestAmount;
    @NotNull
    private BigDecimal totalAmount;
    @NotNull
    private Integer term;
    @NotNull
    private BigDecimal monthlyPayment;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;
}

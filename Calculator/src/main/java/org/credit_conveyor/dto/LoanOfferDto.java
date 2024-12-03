package org.credit_conveyor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
@Data
public class LoanOfferDto {
    //сюда не добавляю аннотации @notNull, потому что проверяю это в прескоринге
    private UUID statementId;
    private BigDecimal requestAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}

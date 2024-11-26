package org.credit_conveyor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class CreditDto {
    private LocalDate date;
    private BigDecimal principalPayment;
    private BigDecimal interestPayment;
    private BigDecimal totalPayment;
    private BigDecimal remainingDebt;
}

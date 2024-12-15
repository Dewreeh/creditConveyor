package org.deal.model;

import jakarta.persistence.*;
import org.deal.dto.PaymentScheduleElementDto;
import org.deal.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "credit")
public class Credit {
        @Id
        private UUID creditId;

        private BigDecimal amount;

        private Integer term;

        private BigDecimal monthlyPayment;

        private BigDecimal rate;

        private BigDecimal psk;

        private String paymentSchedule;
        private Boolean insuranceEnabled;

        @Enumerated(EnumType.STRING)
        private CreditStatus creditStatus;
}

package org.deal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.deal.dto.PaymentScheduleElementDto;
import org.deal.enums.CreditStatus;
import org.deal.repository.LoanOfferAttributeConverter;
import org.deal.repository.PaymentScheduleConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Entity
@Data
@Table(name = "credit")
public class Credit {
        @Id
        private UUID creditId;

        private BigDecimal amount;

        private Integer term;

        private BigDecimal monthlyPayment;

        private BigDecimal rate;

        private BigDecimal psk;

        @Column(name = "payment_schedule", columnDefinition = "jsonb")
        @Convert(converter = PaymentScheduleConverter.class)
        private List<PaymentScheduleElementDto> paymentSchedule;

        private Boolean insuranceEnabled;

        @Enumerated(EnumType.STRING)
        private CreditStatus creditStatus;
}

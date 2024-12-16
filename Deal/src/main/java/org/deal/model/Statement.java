package org.deal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.deal.dto.LoanOfferDto;
import org.deal.repository.LoanOfferAttributeConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name="statement")
public class Statement {
    @Id
    private UUID statementId;

    @ManyToOne
    @JoinColumn(name = "clientId",  referencedColumnName = "clientId")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "creditId",  referencedColumnName = "creditId")
    private Credit credit;

    private String status;

    private LocalDateTime creationDate;

    @Column(name = "applied_offer", columnDefinition = "jsonb")
    @Convert(converter = LoanOfferAttributeConverter.class)
    private LoanOfferDto appliedOffer;

    private LocalDateTime signDate;

    private UUID sesCode;

    private String statusHistory;
}

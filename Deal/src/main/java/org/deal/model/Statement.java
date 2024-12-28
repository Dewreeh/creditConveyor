package org.deal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.repository.LoanOfferAttributeConverter;
import org.deal.repository.StatusHistoryConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    private LocalDateTime creationDate;

    @Column(name = "applied_offer", columnDefinition = "jsonb")
    @JdbcTypeCode( SqlTypes.JSON  )
    private LoanOfferDto appliedOffer;

    private LocalDateTime signDate;

    private UUID sesCode;

    @Convert(converter = StatusHistoryConverter.class)
    @JdbcTypeCode( SqlTypes.JSON )
    private List<StatementStatusHistoryDto> statusHistory;
}

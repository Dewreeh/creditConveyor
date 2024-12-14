package org.deal.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="statement")
public class Statement {
    @Id
    private UUID statementId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "credit_id", nullable = false)
    private Credit credit;

    private String status;

    private LocalDateTime creationDate;

    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private String appliedOffer;

    private LocalDateTime signDate;

    private UUID sesCode;

    private String statusHistory;
}

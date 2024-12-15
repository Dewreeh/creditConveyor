package org.deal.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name="passport")
public class Passport {
    @Id
    private UUID passportUuid;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
    private String additionalInfo;

}

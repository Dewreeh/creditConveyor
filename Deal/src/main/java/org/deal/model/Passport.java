package org.deal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;
@Entity
@Data
@Table(name="passport")
public class Passport {
    @Id
    private UUID passportUuid;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;

}

package org.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "employment")
public class Employment {
    @Id
    private UUID employmentUuid;

    private String status;

    private String employerInn;
    private BigDecimal salary;
    private String position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}

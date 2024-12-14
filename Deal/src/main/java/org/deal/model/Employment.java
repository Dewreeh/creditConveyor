package org.deal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "employment")
public class Employment {
    @Id
    private UUID employmentUuid;

    private String status;

    private String employerInn;
    private Double salary;
    private String position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}

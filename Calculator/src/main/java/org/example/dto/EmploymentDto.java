package org.example.dto;

import lombok.Data;
import org.example.enums.EmploymentStatus;
import org.example.enums.Position;

import java.math.BigDecimal;
@Data
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}

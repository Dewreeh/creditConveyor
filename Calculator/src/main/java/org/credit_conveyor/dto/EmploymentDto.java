package org.credit_conveyor.dto;

import lombok.Data;
import org.credit_conveyor.enums.EmploymentStatus;
import org.credit_conveyor.enums.Position;

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

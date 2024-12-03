package org.credit_conveyor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.credit_conveyor.enums.EmploymentStatus;
import org.credit_conveyor.enums.Position;

import java.math.BigDecimal;
@Data
public class EmploymentDto {
    @NotNull
    private EmploymentStatus employmentStatus;
    @NotNull
    private String employerINN;
    @NotNull
    private BigDecimal salary;
    @NotNull
    private Position position;
    @NotNull
    private Integer workExperienceTotal;
    @NotNull
    private Integer workExperienceCurrent;
}

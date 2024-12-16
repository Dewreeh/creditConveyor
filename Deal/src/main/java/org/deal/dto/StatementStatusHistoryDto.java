package org.deal.dto;

import lombok.Data;
import org.deal.enums.ChangeType;

import java.util.Date;

@Data
public class StatementStatusHistoryDto {
    private String status;
    private Date time;
    private ChangeType changeType;
}
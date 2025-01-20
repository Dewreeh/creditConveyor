package org.dossier.dto;

import lombok.Data;
import org.dossier.Enum.Theme;

@Data
public class EmailMessageDto {
    private String address;
    private Theme theme;
    private Long statementId;
    private String text;
}


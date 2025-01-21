package org.dossier.dto;

import lombok.Data;
import org.dossier.enums.Theme;

import java.util.UUID;

@Data
public class EmailMessageDto {
    private String address;
    private Theme theme;
    private UUID statementId;
    private String text;
}


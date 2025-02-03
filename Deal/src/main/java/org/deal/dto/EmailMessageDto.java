package org.deal.dto;

import lombok.Data;
import org.deal.enums.Theme;

import java.util.UUID;

@Data
public class EmailMessageDto {
    public EmailMessageDto(String address, Theme theme, UUID statementId, String text) {
        this.address = address;
        this.theme = theme;
        this.statementId = statementId;
        this.text = text;
    }


    String address;
    Theme theme;
    UUID statementId;
    String text;
}

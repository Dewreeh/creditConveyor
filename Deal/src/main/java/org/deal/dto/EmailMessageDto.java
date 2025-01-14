package org.deal.dto;

import lombok.Data;
import org.deal.enums.Theme;

import java.util.UUID;

@Data
public class EmailMessageDto {
    public EmailMessageDto(String adress, Theme theme, UUID statementId, String text) {
        this.adress = adress;
        this.theme = theme;
        this.statementId = statementId;
        this.text = text;
    }

    public EmailMessageDto(){

    }

    String adress;
    Theme theme;
    UUID statementId;
    String text;
}

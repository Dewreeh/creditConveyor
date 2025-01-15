package org.deal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.deal.enums.Theme;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "address", "theme", "statementId", "text" })
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

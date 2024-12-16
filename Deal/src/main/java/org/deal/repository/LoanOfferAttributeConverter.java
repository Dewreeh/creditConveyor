package org.deal.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.deal.dto.LoanOfferDto;

//Взято отсюда https://www.baeldung.com/spring-boot-jpa-storing-postgresql-jsonb
@Converter(autoApply = true)
public class LoanOfferAttributeConverter implements AttributeConverter<LoanOfferDto, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LoanOfferDto dto) {
        // Если объект null, возвращаем пустой json, иначе сериализуем в JSON из объетка
        if (dto == null) {
            return "{}";  // Пустой объект JSON вместо null
        }
        try {
            return objectMapper.writeValueAsString(dto);  // Сериализация объекта в JSONB
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException("Ошибка при конвертации LoanOfferDto в JSONB", jpe);
        }
    }

    @Override
    public LoanOfferDto convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty() || value.equals("{}")) {
            return new LoanOfferDto();  // Если значение пустое или пустой объект, возвращаем пустой LoanOfferDto
        }
        try {
            return objectMapper.readValue(value, LoanOfferDto.class);  // Десериализация из JSON в объект
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка при конвертации JSON в LoanOfferDto", e);
        }
    }
}
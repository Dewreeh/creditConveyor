package org.deal.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.deal.dto.PaymentScheduleElementDto;

//Взято отсюда https://www.baeldung.com/spring-boot-jpa-storing-postgresql-jsonb + Добавлена поддержка LocalDateTime в jackson
@Converter(autoApply = true)
public class PaymentScheduleConverter implements AttributeConverter<PaymentScheduleElementDto, String> {
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(PaymentScheduleElementDto dto) {
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
    public PaymentScheduleElementDto convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty() || value.equals("{}")) {
            return new PaymentScheduleElementDto();  // Если значение пустое или пустой объект, возвращаем пустой LoanOfferDto
        }
        try {
            return objectMapper.readValue(value, PaymentScheduleElementDto.class);  // Десериализация из JSON в объект
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка при конвертации JSON в LoanOfferDto", e);
        }
    }
}
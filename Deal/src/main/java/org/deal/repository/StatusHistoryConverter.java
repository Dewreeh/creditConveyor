package org.deal.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.deal.dto.StatementStatusHistoryDto;

import java.util.List;

@Converter(autoApply = true)
public class StatusHistoryConverter implements AttributeConverter<List<StatementStatusHistoryDto>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<StatementStatusHistoryDto> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            return "[]"; // Сохраняем пустой массив, если список пуст
        }
        try {
            return objectMapper.writeValueAsString(historyList);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка при сериализации списка истории статусов в JSON", e);
        }
    }

    @Override
    public List<StatementStatusHistoryDto> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of(); // Возвращаем пустой список, если данных нет
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<StatementStatusHistoryDto>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка при десериализации JSON в список истории статусов", e);
        }
    }
}
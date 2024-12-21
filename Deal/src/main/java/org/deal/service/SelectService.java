package org.deal.service;

import lombok.extern.slf4j.Slf4j;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.ChangeType;
import org.deal.model.Statement;
import org.deal.repository.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class SelectService {
    StatementRepository statementRepository;
    @Autowired
    public SelectService(StatementRepository statementRepository){
        this.statementRepository = statementRepository;
    }

    @Transactional
    public void applyOffer(LoanOfferDto dto) {
        // Получаем заявление по statementId
        Statement statement = statementRepository.getByStatementId(dto.getStatementId());

        // Обновляем предложенные условия
        statement.setAppliedOffer(dto);

        StatementStatusHistoryDto statusHistoryDto = createStatusHistory(ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        // Получаем текущую историю статусов
        List<StatementStatusHistoryDto> statusHistory = statement.getStatusHistory();
        if (statusHistory == null) {
            statusHistory = new ArrayList<>();  // Если истории нет, создаем новый список
        }

        // Добавляем новый элемент в историю
        statusHistory.add(statusHistoryDto);

        // Обновляем историю в заявке
        statement.setStatusHistory(statusHistory);

        statementRepository.save(statement);
    }
    private StatementStatusHistoryDto createStatusHistory(ApplicationStatus status, ChangeType changeType) {
        StatementStatusHistoryDto statusHistoryDto = new StatementStatusHistoryDto();
        statusHistoryDto.setStatus(status.name());
        statusHistoryDto.setTime(new Date());
        statusHistoryDto.setChangeType(changeType);
        return statusHistoryDto;
    }
}




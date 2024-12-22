package org.statement.service;

import org.springframework.stereotype.Service;
import org.statement.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
@Service
public class PrescoringService {
    public Boolean doPrescoring(LoanStatementRequestDto dto){
        boolean isValid = true;

        if(dto.getFirstName() == null || dto.getFirstName().length() < 2 || dto.getFirstName().length() > 30){
            isValid = false;
        }
        if(dto.getLastName() == null || dto.getLastName().length() < 2 || dto.getLastName().length() > 30){
            isValid = false;
        }
        if(dto.getMiddleName() == null || dto.getMiddleName().length() < 2 || dto.getMiddleName().length() > 30){
            isValid = false;
        }
        if(dto.getAmount() == null || dto.getAmount().compareTo(new BigDecimal("20000")) < 0){
            isValid = false;
        }
        if(dto.getTerm() == null || dto.getTerm() < 6){
            isValid = false;
        }
        if (dto.getEmail() == null || !dto.getEmail().matches("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")){
            isValid = false;
        }

        if(dto.getPassportSeries() == null || !dto.getPassportSeries().matches("\\d{4}")){
            isValid = false;
        }
        if(dto.getPassportNumber() == null || !dto.getPassportNumber().matches("\\d{6}")){
            isValid = false;
        }

        LocalDate minDate = LocalDate.now().minusYears(18);
        if(dto.getBirthdate() == null || minDate.isBefore(dto.getBirthdate())){
            isValid = false;
        }
        return isValid;
    }

}

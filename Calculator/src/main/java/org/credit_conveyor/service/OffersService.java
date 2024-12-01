package org.credit_conveyor.service;


import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OffersService {
    @Value("${application.baseRate}")
    private BigDecimal baseRate;
    private LoanOfferDto loanOfferDto;
    private List<LoanOfferDto> loanOffers;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){

        return new ArrayList<>();
    }


}

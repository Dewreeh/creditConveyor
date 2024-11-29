package org.credit_conveyor.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class OffersService {
    @Value("${application.baseRate}")
    private BigDecimal baseRate;
}

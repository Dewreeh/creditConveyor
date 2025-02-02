import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.credit_conveyor.service.OffersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class OfferServiceTest {
    @Mock
    private LoanStatementRequestDto dto;

    @InjectMocks
    private OffersService offersService;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        offersService = new OffersService();
        offersService.setBaseRate(new BigDecimal("10.00"));
        offersService.setInsuranceCost(new BigDecimal("500.00"));
    }
    @Test
    void testGetOffers(){
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        dto.setAmount(new BigDecimal("100000"));
        dto.setTerm(12);
        dto.setFirstName("Bober");
        dto.setLastName("Kurwa");
        dto.setMiddleName("Jakibydlo");
        dto.setEmail("bober@bobermail.ru");
        dto.setBirthdate(LocalDate.of(1990, 5, 15));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("567890");
        List<LoanOfferDto> offers = offersService.getOffers(dto);

        assertNotNull(offers);
        assertEquals(4, offers.size());

    }


}

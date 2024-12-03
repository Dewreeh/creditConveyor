import org.credit_conveyor.controller.CalculatorController;
import org.credit_conveyor.dto.LoanOfferDto;
import org.credit_conveyor.dto.LoanStatementRequestDto;
import org.credit_conveyor.service.CalcService;
import org.credit_conveyor.service.OffersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

public class CalculatorControllerTest {
    @Mock
    private OffersService offersService;
    @Mock
    private CalcService calcService;

    @InjectMocks
    private CalculatorController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test
    void testOffers(){
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

        when(offersService.isValid(dto)).thenReturn(true);
        when(offersService.getOffers(dto)).thenReturn(List.of(new LoanOfferDto(), new LoanOfferDto()));


    }

}

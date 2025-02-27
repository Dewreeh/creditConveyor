

import org.deal.dto.LoanOfferDto;
import org.deal.model.Statement;
import org.deal.repository.StatementRepository;
import org.deal.service.SelectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelectServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private SelectService selectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }




    @Test
    void testApplyOffer_NullDto() {
        assertThrows(NullPointerException.class, () -> selectService.applyOffer(null));
        verify(statementRepository, never()).save(any());
    }



    @Test
    void testApplyOffer_SaveException() {
        LoanOfferDto dto = createLoanOfferDto();
        Statement statement = createStatement();

        when(statementRepository.getByStatementId(dto.getStatementId())).thenReturn(statement);
        doThrow(RuntimeException.class).when(statementRepository).save(any());

        assertThrows(RuntimeException.class, () -> selectService.applyOffer(dto));
    }

    private LoanOfferDto createLoanOfferDto() {
        LoanOfferDto dto = new LoanOfferDto();
        dto.setStatementId(UUID.randomUUID());
        dto.setRequestAmount(BigDecimal.valueOf(100000));
        dto.setTotalAmount(BigDecimal.valueOf(120000));
        dto.setTerm(12);
        dto.setMonthlyPayment(BigDecimal.valueOf(10000));
        dto.setRate(BigDecimal.valueOf(12.5));
        dto.setIsInsuranceEnabled(true);
        dto.setIsSalaryClient(false);
        return dto;
    }

    private Statement createStatement() {
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setStatusHistory(new ArrayList<>());
        return statement;
    }
}

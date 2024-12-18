

import org.deal.dto.CreditDto;
import org.deal.dto.FinishRegistrationRequestDto;
import org.deal.dto.LoanOfferDto;
import org.deal.dto.ScoringDataDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.CreditStatus;
import org.deal.enums.Gender;
import org.deal.enums.MaritalStatus;
import org.deal.model.Client;
import org.deal.model.Credit;
import org.deal.model.Statement;
import org.deal.repository.CreditRepository;
import org.deal.repository.StatementRepository;
import org.deal.service.FinishRegistartionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinishRegistrationServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private FinishRegistartionService finishRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testFinishRegistration_StatementNotFound() {
        UUID statementUuid = UUID.randomUUID();
        FinishRegistrationRequestDto requestDto = createFinishRegistrationRequestDto();

        when(statementRepository.getByStatementId(statementUuid)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> finishRegistrationService.finishRegistration(requestDto, statementUuid));

        assertEquals("Заявка с таким ID не найдена", exception.getReason());
        assertEquals(404, exception.getStatusCode().value());
        verify(statementRepository, never()).save(any());
        verify(creditRepository, never()).save(any());
    }



    private Statement createStatement() {
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(createClient());
        statement.setAppliedOffer(createLoanOfferDto());
        return statement;
    }

    private Client createClient() {
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setFirstName("John");
        client.setMiddleName("A.");
        client.setLastName("Doe");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        return client;
    }

    private LoanOfferDto createLoanOfferDto() {
        LoanOfferDto loanOffer = new LoanOfferDto();
        loanOffer.setTotalAmount(BigDecimal.valueOf(100000));
        loanOffer.setTerm(12);
        loanOffer.setIsInsuranceEnabled(true);
        loanOffer.setIsSalaryClient(false);
        return loanOffer;
    }

    private FinishRegistrationRequestDto createFinishRegistrationRequestDto() {
        FinishRegistrationRequestDto requestDto = new FinishRegistrationRequestDto();
        requestDto.setGender(Gender.valueOf("MALE"));
        requestDto.setPassportIssueDate(LocalDate.of(2015, 6, 15));
        requestDto.setPassportIssueBranch("123-456");
        requestDto.setMaritalStatus(MaritalStatus.valueOf("SINGLE"));
        requestDto.setDependentAmount(0);
        return requestDto;
    }

    private ScoringDataDto createScoringDataDto() {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(BigDecimal.valueOf(100000));
        scoringDataDto.setTerm(12);
        scoringDataDto.setFirstName("John");
        scoringDataDto.setMiddleName("A.");
        scoringDataDto.setLastName("Doe");
        return scoringDataDto;
    }

    private CreditDto createCreditDto() {
        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(BigDecimal.valueOf(100000));
        creditDto.setTerm(12);
        creditDto.setMonthlyPayment(BigDecimal.valueOf(8500));
        creditDto.setPsk(BigDecimal.valueOf(0.12));
        return creditDto;
    }

    private Credit createCredit() {
        Credit credit = new Credit();
        credit.setCreditId(UUID.randomUUID());
        credit.setAmount(BigDecimal.valueOf(100000));
        credit.setTerm(12);
        credit.setMonthlyPayment(BigDecimal.valueOf(8500));
        credit.setPsk(BigDecimal.valueOf(0.12));
        credit.setCreditStatus(CreditStatus.CALCULATED);
        return credit;
    }
}

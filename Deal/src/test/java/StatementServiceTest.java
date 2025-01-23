

import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.dto.StatementStatusHistoryDto;
import org.deal.enums.ApplicationStatus;
import org.deal.enums.ChangeType;
import org.deal.model.Client;
import org.deal.model.Statement;
import org.deal.repository.StatementRepository;
import org.deal.service.ClientService;
import org.deal.service.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private StatementService statementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testSaveStatementSuccess() {
        Client client = createValidClient();
        Statement statement = new Statement();
        UUID statementId = UUID.randomUUID();
        statement.setStatementId(statementId);
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statement.setCreationDate(LocalDateTime.now());
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        UUID result = statementService.saveStatement(client);
        assertNotNull(result);
        verify(statementRepository, times(1)).save(any(Statement.class));
    }



    @Test
    void testGetStatementSuccess() throws Exception {
        UUID statementId = UUID.randomUUID();
        Statement statement = new Statement();
        statement.setStatementId(statementId);

        when(statementRepository.getByStatementId(statementId)).thenReturn(statement);

        Statement result = statementService.getStatement(statementId);

        assertNotNull(result);
        assertEquals(statementId, result.getStatementId());
        verify(statementRepository, times(1)).getByStatementId(statementId);
    }

    @Test
    void testGetStatementNotFound() {
        UUID statementId = UUID.randomUUID();

        when(statementRepository.getByStatementId(statementId)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> statementService.getStatement(statementId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Заявка с таким ID не найдена", exception.getReason());
    }

    @Test
    void testSetUuidForOffers() {
        UUID statementUuid = UUID.randomUUID();
        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();

        List<LoanOfferDto> offers = List.of(offer1, offer2);

        List<LoanOfferDto> result = statementService.setUuidForOffers(offers, statementUuid);

        assertNotNull(result);
        assertEquals(statementUuid, result.get(0).getStatementId());
        assertEquals(statementUuid, result.get(1).getStatementId());
    }

    @Test
    void testSetUuidForOffersWithEmptyList() {
        UUID statementUuid = UUID.randomUUID();
        List<LoanOfferDto> offers = Collections.emptyList();

        List<LoanOfferDto> result = statementService.setUuidForOffers(offers, statementUuid);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    private Client createValidClient() {
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setFirstName("Valera");
        client.setLastName("Valera");
        return client;
    }
}

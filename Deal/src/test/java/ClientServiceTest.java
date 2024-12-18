

import org.deal.dto.LoanStatementRequestDto;
import org.deal.model.Client;
import org.deal.model.Passport;
import org.deal.repository.ClientRepository;
import org.deal.repository.PassportRepository;
import org.deal.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PassportRepository passportRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClient_Success() {
        LoanStatementRequestDto dto = createRequestDto();
        Passport passport = createPassport();
        Client client = createClient(dto, passport);

        when(passportRepository.save(any(Passport.class))).thenReturn(passport);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client savedClient = clientService.saveClient(dto);

        assertNotNull(savedClient);
        assertEquals(dto.getEmail(), savedClient.getEmail());
        verify(passportRepository, times(1)).save(any(Passport.class));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testSaveClient_ClientAlreadyExists() {
        LoanStatementRequestDto dto = createRequestDto();
        Passport passport = createPassport();

        when(passportRepository.save(any(Passport.class))).thenReturn(passport);
        when(clientRepository.save(any(Client.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> clientService.saveClient(dto));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Клиент уже существует", exception.getReason());
        verify(clientRepository, times(1)).save(any(Client.class));
    }



    @Test
    void testSavePassport_Success() {
        LoanStatementRequestDto dto = createRequestDto();
        Passport passport = createPassport();

        when(passportRepository.save(any(Passport.class))).thenReturn(passport);

        Passport savedPassport = clientService.savePassport(dto);

        assertNotNull(savedPassport);
        assertEquals(dto.getPassportSeries(), savedPassport.getSeries());
        assertEquals(dto.getPassportNumber(), savedPassport.getNumber());
        verify(passportRepository, times(1)).save(any(Passport.class));
    }

    @Test
    void testSavePassport_PassportAlreadyExists() {
        LoanStatementRequestDto dto = createRequestDto();

        when(passportRepository.save(any(Passport.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> clientService.savePassport(dto));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Паспорт уже существует", exception.getReason());
        verify(passportRepository, times(1)).save(any(Passport.class));
    }

    private LoanStatementRequestDto createRequestDto() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setEmail("test@example.com");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMiddleName("Smith");
        dto.setPassportSeries("1234");
        dto.setPassportNumber("567890");
        return dto;
    }

    private Passport createPassport() {
        Passport passport = new Passport();
        passport.setPassportUuid(UUID.randomUUID());
        passport.setSeries("1234");
        passport.setNumber("567890");
        return passport;
    }

    private Client createClient(LoanStatementRequestDto dto, Passport passport) {
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setEmail(dto.getEmail());
        client.setBirthDate(dto.getBirthdate());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());
        client.setPassport(passport);
        return client;
    }
}

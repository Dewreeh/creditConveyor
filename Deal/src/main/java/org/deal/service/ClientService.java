package org.deal.service;

import org.deal.dto.LoanStatementRequestDto;
import org.deal.model.Client;
import org.deal.model.Passport;
import org.deal.repository.ClientRepository;
import org.deal.repository.PassportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PassportRepository passportRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, PassportRepository passportRepository) {
        this.clientRepository = clientRepository;
        this.passportRepository = passportRepository;
    }

    @Transactional
    public Client saveClient(LoanStatementRequestDto dto) {
        Passport passport = savePassport(dto);
        Client client = createClientEntity(dto, passport);

        try {
            clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Клиент уже существует", e);
        }

        return client;
    }

    private Client createClientEntity(LoanStatementRequestDto dto, Passport passport) {
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

    @Transactional
    public Passport savePassport(LoanStatementRequestDto dto) {
        Passport passport = createPassportEntity(dto);

        try {
            passportRepository.save(passport);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Паспорт уже существует", e);
        }

        return passport;
    }

    private Passport createPassportEntity(LoanStatementRequestDto dto) {
        Passport passport = new Passport();
        passport.setPassportUuid(UUID.randomUUID());
        passport.setSeries(dto.getPassportSeries());
        passport.setNumber(dto.getPassportNumber());
        return passport;
    }
}

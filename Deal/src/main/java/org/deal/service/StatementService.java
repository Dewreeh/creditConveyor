package org.deal.service;

import org.deal.dto.LoanOfferDto;
import org.deal.dto.LoanStatementRequestDto;
import org.deal.model.Client;
import org.deal.model.Statement;
import org.deal.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class StatementService {

    private final RestClient restClient;
    private final ClientRepository clientRepository;

    @Autowired
    public StatementService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.restClient = RestClient.builder().build();
    }

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        List<LoanOfferDto> offers = getOffersFromCalculatorMicroservice(dto);
        return offers;
    }


    public void saveClient(LoanStatementRequestDto dto){
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        client.setEmail(dto.getEmail());
        client.setEmployment(null);
        client.setGender(null);
        client.setBirthDate(dto.getBirthdate());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());
        client.setPassport(null); //у нас нет инфы про паспорт, кроме серии и номера
        client.setAccountNumber(null);
        try {
            clientRepository.save(client);
        } catch(DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate data", e);
        }

    }

    public void saveStatement(LoanStatementRequestDto dto){
        Statement statement = new Statement();
    }

    private List<LoanOfferDto> getOffersFromCalculatorMicroservice(LoanStatementRequestDto dto){
        return restClient.post()
                .uri("http://localhost:8080/calculator/offers")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(dto)
                .retrieve()
                .body(List.class);
    }


}

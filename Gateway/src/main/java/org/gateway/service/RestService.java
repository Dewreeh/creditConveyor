package org.gateway.service;

import org.gateway.dto.FinishRegistrationRequestDto;
import org.gateway.dto.LoanOfferDto;
import org.gateway.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
public class RestService {

    private final RestClient restClient;
    @Value("${deal.domain}")
    private String dealDomain;
    @Value("${statement.domain}")
    private String statementDomain;

    public RestService() {
        this.restClient = RestClient.builder().build();
    }

    public ResponseEntity<String> sendRequestToDealCalculate(UUID statementId, FinishRegistrationRequestDto dto){
         return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8090)
                        .path("/deal/offer/calculate")
                        .queryParam("statementId", statementId.toString())
                        .build())
                .body(dto)
                .retrieve()
                 .toEntity(String.class);

    }

    public List<LoanOfferDto> sendRequestToStatementStatement(LoanStatementRequestDto dto){
        return restClient.post()
                .uri(statementDomain + "/statement")
                .body(dto)
                .retrieve().
                body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    public ResponseEntity<String> sendRequestToStatementOffer(LoanOfferDto dto){
        return restClient.post()
                .uri(statementDomain + "/statement/select/offer")
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RuntimeException("Ошибка на сервере: " + response.getBody());
                })
                .toEntity(String.class);
    }

    public ResponseEntity<String> sendRequestToDealDocumentSend(UUID statementId){
        String url = dealDomain + "/deal/document/" + statementId + "/send";
        return restClient.post()
                .uri(url)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> sendRequestToDealDocumentSign(UUID statementId){
        String url = dealDomain + "/deal/document/" + statementId + "/sign";
        return restClient.post()
                .uri(url)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> sendRequestToDealDocumentCode(UUID statementId, UUID ses_code){
        String url = dealDomain + "/deal/document/" + statementId + "/" + ses_code;
        return restClient.post()
                .uri(url)
                .retrieve()
                .toEntity(String.class);
    }
}

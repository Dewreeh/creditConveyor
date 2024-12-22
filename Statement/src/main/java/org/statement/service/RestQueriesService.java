package org.statement.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.statement.dto.LoanOfferDto;
import org.statement.dto.LoanStatementRequestDto;

import java.util.List;

@Service
public class RestQueriesService {


    private final RestClient restClient;

    public RestQueriesService(){
        this.restClient = RestClient.builder().build();
    }

    public List<LoanOfferDto> getOffersFromDeal(LoanStatementRequestDto dto){
       return restClient.post()
                .uri("http://localhost:8080/deal/offer")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
     }
}

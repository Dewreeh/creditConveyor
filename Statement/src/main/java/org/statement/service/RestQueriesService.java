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
        try {
            return restClient.post()
                    .uri("http://localhost:8090/deal/statement")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить офферы", e);
        }
     }

     public void selectOffer(LoanOfferDto dto){
         try {
             restClient.post()
                     .uri("http://localhost:8090/deal/offer/select")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(dto)
                     .retrieve()
                     .toBodilessEntity();
         } catch (Exception e) {
             throw new RuntimeException("Произошла ошибка при выборе оффера", e);
         }
     }
}

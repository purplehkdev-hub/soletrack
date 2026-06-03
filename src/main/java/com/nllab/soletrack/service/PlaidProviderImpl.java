package com.nllab.soletrack.service;

import com.nllab.soletrack.model.BalanceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component("plaid")
public class PlaidProviderImpl implements OpenBankingProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${plaid.base-url:}")
    private String baseUrl;

    @Value("${plaid.access-token:}")
    private String accessToken;

    @Override
    public BalanceResponse getAccountBalance(String accountId) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return new BalanceResponse(accountId, new BigDecimal("500.00"), "USD");
        }

        String url = baseUrl + "/accounts/{id}/balance";
        try {
            BalanceResponse resp = restTemplate.getForObject(url, BalanceResponse.class, accountId);
            if (resp == null) {
                return new BalanceResponse(accountId, BigDecimal.ZERO, "USD");
            }
            return resp;
        } catch (RestClientException e) {
            throw new RuntimeException("Plaid provider request failed", e);
        }
    }

    @Override
    public String getProviderName() {
        return "plaid";
    }

    @Override
    public Mono<String> getAuthUrl() {
        return null;
    }

    @Override
    public Mono<Map<String, Object>> createSession(String code) {
        return null;
    }

    @Override
    public Mono<Map<String, Object>> getBalances(String accountId) {
        return null;
    }


}
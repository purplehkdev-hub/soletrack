package com.nllab.soletrack.service;

import com.nllab.soletrack.model.dto.BalanceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("plaid")
public class PlaidProviderImpl implements OpenBankingProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${plaid.base-url:}")
    private String baseUrl;

    @Value("${plaid.access-token:}")
    private String accessToken;

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
    public Mono<BalanceResponse> getBalances(String accountId) {
        return null;
    }


}
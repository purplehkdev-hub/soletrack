package com.nllab.soletrack.service;

import com.nllab.soletrack.model.BalanceResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OpenBankingProvider {
    /**
     * Retrieve account balance for given account ID.
     * Implementations should return a BalanceResponse or throw a runtime exception on unrecoverable errors.
     */
    BalanceResponse getAccountBalance(String accountId);

    /**
     * Provider key/name used by the factory (e.g., "enableBanking", "plaid").
     */
    String getProviderName();

    Mono<String> getAuthUrl();

    Mono<Map<String, Object>> createSession(String code);

    Mono<Map<String, Object>> getBalances(String accountId);

}
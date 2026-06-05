package com.nllab.soletrack.service;

import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.model.dto.BankSessionResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OpenBankingProvider {

    /**
     * Provider key/name used by the factory (e.g., "enableBanking", "plaid").
     */
    String getProviderName();

    Mono<String> getAuthUrl();

    Mono<BankSessionResponse> createSession(String code);

    Mono<BalanceResponse> getBalances(String accountId);

}
package com.nllab.soletrack.service;

import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.model.dto.BankSessionResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class BankingService {

    private final BankingProviderFactory providerFactory;

    public BankingService(BankingProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }


    public Mono<String> getAuthUrl(){
        OpenBankingProvider provider = providerFactory.getProvider();
        return provider.getAuthUrl();
    }

    public Mono<BankSessionResponse> createSession(String code) {
        OpenBankingProvider provider = providerFactory.getProvider();
        return provider.createSession(code);
    }

    public Mono<BalanceResponse> getBalances(String accountUid) {
        OpenBankingProvider provider = providerFactory.getProvider();
        return provider.getBalances(accountUid);
    }
}
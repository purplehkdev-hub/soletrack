package com.nllab.soletrack.service;

import com.nllab.soletrack.model.BalanceResponse;
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


    public BalanceResponse getAccountBalance(String accountId) {
        OpenBankingProvider provider = providerFactory.getProvider();
        try {
            return provider.getAccountBalance(accountId);
        } catch (RuntimeException e) {
            // try other available providers as fallback
            for (OpenBankingProvider p : providerFactory.getAllProviders()) {
                if (p == provider) continue;
                try {
                    return p.getAccountBalance(accountId);
                } catch (RuntimeException ignored) {
                    // continue trying
                }
            }
            // last resort: return zero balance
            return new BalanceResponse(accountId, java.math.BigDecimal.ZERO, "USD");
        }
    }

    public Mono<Map<String, Object>> createSession(String code) {
        OpenBankingProvider provider = providerFactory.getProvider();
        return provider.createSession(code);
    }

    public Mono<Map<String, Object>> getBalances(String accountUid) {
        OpenBankingProvider provider = providerFactory.getProvider();
        return provider.getBalances(accountUid);
    }
}
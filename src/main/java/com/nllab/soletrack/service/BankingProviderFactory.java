package com.nllab.soletrack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class BankingProviderFactory {

    private final Map<String, OpenBankingProvider> providers;
    private final String activeProviderKey;

    public BankingProviderFactory(Map<String, OpenBankingProvider> providers,
                                  @Value("${open-banking.active-provider:enableBanking}") String activeProviderKey) {
        this.providers = providers;
        this.activeProviderKey = activeProviderKey;
    }

    public OpenBankingProvider getProvider() {
        OpenBankingProvider p = providers.get(activeProviderKey);
        if (p != null) return p;
        // fallback to first available
        return providers.values().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No OpenBankingProvider beans available"));
    }

    public Collection<OpenBankingProvider> getAllProviders() {
        return providers.values();
    }
}
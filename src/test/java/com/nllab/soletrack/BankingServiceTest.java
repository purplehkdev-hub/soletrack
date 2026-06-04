package com.nllab.soletrack;

import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.service.BankingService;
import com.nllab.soletrack.service.BankingProviderFactory;
import com.nllab.soletrack.service.OpenBankingProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BankingServiceTest {

    BankingProviderFactory providerFactory;
    OpenBankingProvider provider;
    BankingService bankingService;

    @BeforeEach
    public void setup() {
        providerFactory = mock(BankingProviderFactory.class);
        provider = mock(OpenBankingProvider.class);
        when(providerFactory.getProvider()).thenReturn(provider);
        bankingService = new BankingService(providerFactory);
    }

    @Test
    public void testGetAuthUrl_delegates() {
        when(provider.getAuthUrl()).thenReturn(Mono.just("http://auth"));
        String url = bankingService.getAuthUrl().block();
        assertEquals("http://auth", url);
    }

    @Test
    public void testCreateSession_delegates() {
        Map<String, Object> mockResponse = Map.of("accounts", java.util.List.of());
        when(provider.createSession("code123")).thenReturn(Mono.just(mockResponse));
        Map<String, Object> res = bankingService.createSession("code123").block();
        assertNotNull(res);
    }

    @Test
    public void testGetBalances_delegates() {
        when(provider.getBalances("acc1")).thenReturn(Mono.just(new BalanceResponse(null)));
        BalanceResponse r = bankingService.getBalances("acc1").block();
        assertNotNull(r);
    }
}
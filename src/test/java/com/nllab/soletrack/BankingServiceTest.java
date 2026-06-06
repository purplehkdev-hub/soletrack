package com.nllab.soletrack;

import com.nllab.soletrack.model.dto.AccountDetail;
import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.model.dto.BankSessionResponse;
import com.nllab.soletrack.service.BankingService;
import com.nllab.soletrack.service.BankingProviderFactory;
import com.nllab.soletrack.service.OpenBankingProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

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
        Mono<String> url = bankingService.getAuthUrl();

        StepVerifier.create(url)
                .assertNext(res -> {
                    assertEquals("http://auth", res);
                })
                .verifyComplete();
    }

    @Test
    public void testCreateSession_delegates() {
        List<AccountDetail> mockResponse = new ArrayList<>(List.of(new AccountDetail("uid", "name")));

        when(provider.createSession("code123")).thenReturn(Mono.just(new BankSessionResponse(mockResponse)));
        Mono<BankSessionResponse> resultMono =  bankingService.createSession("code123");

        StepVerifier.create(resultMono)
                .assertNext(res -> {
                    assertNotNull(res);
                })
                .verifyComplete();
    }

    @Test
    public void testGetBalances_delegates() {
        when(provider.getBalances("acc1")).thenReturn(Mono.just(new BalanceResponse(null)));
        Mono<BalanceResponse> r = bankingService.getBalances("acc1");

        StepVerifier.create(r)
                .assertNext(res -> {
                    assertNotNull(res);
                })
                .verifyComplete();
    }
}
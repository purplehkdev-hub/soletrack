package com.nllab.soletrack;

import com.nllab.soletrack.model.dto.*;
import com.nllab.soletrack.service.BankingService;
import com.nllab.soletrack.controller.EnableBankingController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnableBankingControllerTest {

    BankingService bankingService;
    EnableBankingController controller;

    @BeforeEach
    public void setup() {
        //BankingService bankingService = Mockito.mock(BankingService.class);
        bankingService = mock(BankingService.class);
        controller = new EnableBankingController(bankingService);
    }

    @Test
    public void testGetBalance_success() {
        when(bankingService.getBalances("id1")).thenReturn(Mono.just(new BalanceResponse(null)));

        var respMono = controller.getBalance("id1");

        StepVerifier.create(respMono)
                .assertNext(resp -> {
                    assertNotNull(resp);
                    assertEquals(HttpStatus.OK, resp.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    public void testGetBalance_notFoundOnError() {
        when(bankingService.getBalances("id1")).thenReturn(Mono.error(new RuntimeException("fail")));

        var respMono = controller.getBalance("id1");

        StepVerifier.create(respMono)
                .assertNext(resp -> {
                    assertNotNull(resp);
                    assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
                })
                .verifyComplete();
    }


    @Test
    public void testHandleBankCallback_success() {
        String targetCode = "code123";
        List<AccountDetail> mockResponse = new ArrayList<>(List.of(new AccountDetail("uid", "name")));

        when(bankingService.createSession(targetCode))
                .thenReturn(Mono.just(new BankSessionResponse(mockResponse)));

        Mono<BankCallbackResponse> resultMono = controller.handleBankCallback(targetCode, null);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("SUCCESS", result.status());
                    assertEquals(1, result.accounts().size());
                })
                .verifyComplete();
    }

    @Test
    void testGetAuthUrl_success() {
        String mockUrl = "https://bank.com";
        when(bankingService.getAuthUrl()).thenReturn(Mono.just(mockUrl));
        Mono<CallbackResponse> resultMono = controller.getAuthUrl();
        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    assertNotNull(response);
                })
                .verifyComplete();
    }

    @Test
    void testGetAuthUrl_error() {
        String errorMessage = "Bank provider connection timeout";
        when(bankingService.getAuthUrl()).thenReturn(Mono.error(new RuntimeException(errorMessage)));
        Mono<CallbackResponse> resultMono = controller.getAuthUrl();
        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    assertNotNull(response);
                })
                .verifyComplete();
    }
}
package com.nllab.soletrack;

import com.nllab.soletrack.service.BankingService;
import com.nllab.soletrack.controller.EnableBankingController;
import com.nllab.soletrack.model.dto.BalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnableBankingControllerTest {

    BankingService bankingService;
    EnableBankingController controller;

    @BeforeEach
    public void setup() {
        BankingService bankingService = Mockito.mock(BankingService.class);
        controller = new EnableBankingController(bankingService);
        // inject mock
        try {
            Field f = EnableBankingController.class.getDeclaredField("bankingService");
            f.setAccessible(true);
            f.set(controller, bankingService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetBalance_success() {
        when(bankingService.getBalances("id1")).thenReturn(Mono.just(new BalanceResponse(null)));
        var resp = controller.getBalance("id1").block();
        assertNotNull(resp);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void testGetBalance_notFoundOnError() {
        when(bankingService.getBalances("id1")).thenReturn(Mono.error(new RuntimeException("fail")));
        var resp = controller.getBalance("id1").block();
        assertNotNull(resp);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }


    //@Test
    //public void testHandleBankCallback_success() {
    //    Map<String, Object> account = Map.of("uid", "u1", "name", "n1");
    //    when(bankingService.createSession("code1")).thenReturn(Mono.just(Map.of("accounts", List.of(account))));
//
    //    Object result = controller.handleBankCallback("code1", null).block();
    //    assertTrue(result instanceof Map);
    //    Map<?, ?> m = (Map<?, ?>) result;
    //    assertEquals("SUCCESS", m.get("status"));
    //    assertEquals(1, m.get("totalAccountsFound"));
    //}

    @Test
    public void testGetAuthUrl_endpoint() {
        when(bankingService.getAuthUrl()).thenReturn(Mono.just("http://auth"));
        var response = controller.getAuthUrl().block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("authUrl"));
    }
}
package com.nllab.soletrack.controller;

import com.nllab.soletrack.model.BalanceResponse;
import com.nllab.soletrack.service.BankingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class EnableBankingController {

    private final BankingService service;

    @Autowired
    private BankingService bankingService;

    public EnableBankingController(BankingService service) {
        this.service = service;
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable("id") String id) {
        BalanceResponse resp = service.getAccountBalance(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/login_to_bank")
    public void redirectToBankLogin(HttpServletResponse response) {
        try {
            String officialAuthUrl = bankingService.getAuthUrl().block();
            System.out.println("Bank Auth URL: " + officialAuthUrl);
            response.sendRedirect(officialAuthUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Not able redirect to Bank Auth URL: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public Mono<?> handleBankCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        try {
            return bankingService.createSession(code) // 1:get Session
                    .flatMap(sessionResponse -> {
                        try {
                            // 2: JSON array account list
                            List<Map<String, Object>> accounts = (List<Map<String, Object>>) sessionResponse.get("accounts");

                            if (accounts == null || accounts.isEmpty()) {
                                return Mono.just(Map.of("error", "No account exist."));
                            }

                            // get unit id (uid)
                            String accountUid = (String) accounts.get(0).get("uid");
                            System.out.println("Account info UID: " + accountUid);

                            // 3:API query UID account balance
                            return bankingService.getBalances(accountUid);

                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    })
                    .onErrorResume(error -> Mono.just(Map.of(
                            "status", "ERROR",
                            "message", error.getMessage()
                    )));

        } catch (Exception e) {
            return Mono.just(Map.of("error", "Callback fail: " + e.getMessage()));
        }
    }


    @GetMapping("/getAuthUrl")
    public Mono<ResponseEntity<Map<String, String>>> getAuthUrl() {
        return bankingService.getAuthUrl()
                .map(url -> {
                    Map<String, String> response = Map.of("authUrl", url);
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(ResponseEntity.status(500)
                        .body(Map.of("error", "Get Auth URL failed.")));
    }

    @PostMapping("/verify-callback")
    public Mono<?> verifyBankCallback(@RequestBody BankCallbackRequest requestBody) {

        String code = requestBody.getCode();
        String state = requestBody.getState();

        System.out.println("Code from front end: " + code);

        try {
            return bankingService.createSession(code)
                    .flatMap(sessionResponse -> {
                        try {
                            List<Map<String, Object>> accounts = (List<Map<String, Object>>) sessionResponse.get("accounts");
                            if (accounts == null || accounts.isEmpty()) {
                                return Mono.just(Map.of("status", "FAILED", "message", "沒有找到任何授權帳戶"));
                            }

                            String accountUid = (String) accounts.get(0).get("uid");

                            return bankingService.getBalances(accountUid)
                                    .map(balanceResponse -> Map.of(
                                            "status", "SUCCESS",
                                            "accountId", accountUid,
                                            "balance", balanceResponse // 這會包含真實的金額與貨幣 JSON
                                    ));
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    })
                    .onErrorResume(error -> Mono.just(Map.of(
                            "status", "FAILED",
                            "message", "處理銀行數據時發生錯誤: " + error.getMessage()
                    )));

        } catch (Exception e) {
            return Mono.just(Map.of("status", "FAILED", "message", e.getMessage()));
        }
    }
} 
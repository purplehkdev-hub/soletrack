package com.nllab.soletrack.controller;

import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.service.BankingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Banking", description = "Endpoints to authenticate with banks and fetch account information")
public class EnableBankingController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/accounts/{id}/balance")
    @Operation(summary = "Get account balance", description = "Retrieve the balances for the provided account id. Returns 200 with BalanceResponse JSON when found; 404 if not available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Balance found", content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public Mono<ResponseEntity<BalanceResponse>> getBalance(@PathVariable("id") String id) {
        return bankingService.getBalances(id)
                .map(resp -> ResponseEntity.ok(resp))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @GetMapping("/login_to_bank")
    @Operation(summary = "Redirect to bank login", description = "Fetches provider auth URL and redirects the browser to bank's login page. Throws runtime error on failure.")
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
    @Operation(summary = "Handle bank callback", description = "Exchanges the provided authorization code for a session and returns a simplified list of accounts (uid and name). Responses: SUCCESS, EMPTY, or ERROR.")
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
                                return Mono.just(Map.of("status", "EMPTY", "message", "No account exist."));
                            }

                            List<Map<String, String>> shortAccountList = accounts.stream()
                                    .map(accountMap -> {
                                        String accountUid = (String) accountMap.get("uid");
                                        String accountName = (String) accountMap.get("name");

                                        // 封裝成只包含 uid 與 name 的乾淨小 Map
                                        return Map.of(
                                                "accountUid", accountUid != null ? accountUid : "Unknown UID",
                                                "accountName", accountName != null ? accountName : "Unnamed Account"
                                        );
                                    })
                                    .collect(java.util.stream.Collectors.toList());

                            return Mono.just(Map.of(
                                    "status", "SUCCESS",
                                    "totalAccountsFound", shortAccountList.size(),
                                    "accounts", shortAccountList // 👈 這就是你想要的純 UID + Name 清單！
                            ));
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
    @Operation(summary = "Get auth URL", description = "Returns JSON containing the provider auth URL that client applications can use to redirect users.")
    public Mono<ResponseEntity<Map<String, String>>> getAuthUrl() {
        return bankingService.getAuthUrl()
                .map(url -> {
                    Map<String, String> response = Map.of("authUrl", url);
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(ResponseEntity.status(500)
                        .body(Map.of("error", "Get Auth URL failed.")));
    }

}
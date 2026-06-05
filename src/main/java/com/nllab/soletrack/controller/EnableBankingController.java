package com.nllab.soletrack.controller;

import com.nllab.soletrack.model.dto.AccountDetail;
import com.nllab.soletrack.model.dto.BalanceResponse;
import com.nllab.soletrack.model.dto.BankCallbackResponse;
import com.nllab.soletrack.model.dto.CallbackResponse;
import com.nllab.soletrack.service.BankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Banking", description = "Endpoints to authenticate with banks and fetch account information")
public class EnableBankingController {

    private final BankingService bankingService;

    @GetMapping("/accounts/{id}/getBalance")
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
    public Mono<Void> redirectToBankLogin(ServerHttpResponse response) {
        return bankingService.getAuthUrl()
                .flatMap(officialAuthUrl -> {
                    log.debug("Bank Auth URL: " + officialAuthUrl);
                    // 302 Target URL
                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().setLocation(URI.create(officialAuthUrl));
                    return response.setComplete(); // send complete singal
                })
                .onErrorResume(e -> {
                    log.error("Failed to redirect to Bank Auth URL", e);
                    return Mono.error(new RuntimeException("Not able redirect to Bank Auth URL: " + e.getMessage()));
                });
    }

    @GetMapping("/callback")
    @Operation(summary = "Handle bank callback", description = "Exchanges the provided authorization code for a session and returns a simplified list of accounts (uid and name). Responses: SUCCESS, EMPTY, or ERROR.")
    public Mono<BankCallbackResponse> handleBankCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {
        return bankingService.createSession(code)
                .map(sessionResponse -> {
                    List<AccountDetail> accounts = sessionResponse.accounts();

                    if (accounts == null || accounts.isEmpty()) {
                        return BankCallbackResponse.empty();
                    }

                    return BankCallbackResponse.success(accounts);
                })
                .onErrorResume(error -> {
                    log.error("Error handling bank callback for code: {}", code, error);
                    return Mono.just(BankCallbackResponse.error(error.getMessage()));
                });
    }


    @GetMapping("/getAuthUrl")
    @Operation(summary = "Get auth URL", description = "Returns JSON containing the provider auth URL that client applications can use to redirect users.")
    public Mono<CallbackResponse> getAuthUrl() {
        return bankingService.getAuthUrl()
                .map(CallbackResponse::success)
                .onErrorResume(error -> {
                    log.error("Error handling get Auth url");
                    return Mono.just(CallbackResponse.error(error.getMessage()));
                });
    }

}
package com.nllab.soletrack.service;

import com.nllab.soletrack.model.dto.BalanceResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component("enableBanking")
public class EnableBankingProviderImpl implements OpenBankingProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient = WebClient.builder().build();

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${enablebanking.base-url:}")
    private String baseUrl;

    @Value("${enable-banking.app-id}")
    private String appId;

    @Value("${enable-banking.key-path}")
    private String keyPath;

    @Value("${enable-banking.redirect-url}")
    private String redirectUrl;

    @Value("${enable-banking.aspsp.name}")
    private String aspsp;

    @Value("${enable-banking.aspsp.country}")
    private String country;


    private String generateClientToken() {
        try {
            //File file = ResourceUtils.getFile(keyPath);
            //String privateKeyPem = Files.readString(file.toPath());
            //privateKeyPem = privateKeyPem.replace("-----BEGIN RSA PRIVATE KEY-----", "")
            //        .replace("-----END RSA PRIVATE KEY-----", "")
            //        .replaceAll("\\s", "");
//
            //byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);

            org.springframework.core.io.Resource resource = resourceLoader.getResource(keyPath);

            String privateKeyPem;
            try (java.io.InputStream inputStream = resource.getInputStream()) {
                privateKeyPem = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }

            privateKeyPem = privateKeyPem
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);

            Instant now = Instant.now();
            return Jwts.builder()
                    .setHeaderParam("kid", appId)
                    .setIssuer("enablebanking.com")
                    .setAudience("api.enablebanking.com")
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("generateSandboxJwt fail.", e);
        }
    }

    public Mono<String> getAuthUrl() {
        try {
            String jwtToken = generateClientToken();

            Map<String, Object> requestBody = Map.of(
                    "access", Map.of(
                            "balances", true,
                            "transactions", true,
                            "valid_until", Instant.now().plus(30, ChronoUnit.DAYS).toString()
                    ),
                    "aspsp", Map.of(
                            "name", aspsp,
                            "country", country
                    ),
                    "psu_type", "personal",
                    "redirect_url", redirectUrl,
                    "state", UUID.randomUUID().toString()
            );

            return webClient.post()
                    .uri(baseUrl + "/auth")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(res -> {
                        if (res != null && res.containsKey("url")) {
                            return (String) res.get("url");
                        }
                        throw new RuntimeException("API not found url ");
                    });

        } catch (Exception e) {
            return Mono.error(new RuntimeException("init Open Banking failed", e));
        }
    }



    @Override
    public String getProviderName() {
        return "enableBanking";
    }

    @Override
    public Mono<Map<String, Object>> createSession(String code) {
        String jwtToken = generateClientToken(); // each request need gen JWT

        Map<String, String> requestBody = Map.of("code", code);

        return webClient.post()
                .uri(baseUrl + "/sessions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @Override
    public Mono<BalanceResponse> getBalances(String accountId) {
        String jwtToken = generateClientToken();

        //path /accounts/{account_id}/balances
        return webClient.get()
                .uri(baseUrl + "/accounts/" + accountId + "/balances")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                //.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
                .bodyToMono(BalanceResponse.class)
                .doOnNext(response -> {
                    log.info(response.toString());
                })
                .doOnError(error -> {
                    log.info(error.toString());
                });
    }

}
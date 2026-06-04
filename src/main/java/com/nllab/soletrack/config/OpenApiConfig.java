package com.nllab.soletrack.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Soletrack API")
                        .version("v1")
                        .description("Soletrack Open Banking integration.\n\nUsage:\n- GET /login_to_bank: redirects user to the selected bank's OAuth login page.\n- GET /callback?code=...: exchanges code for a session and returns a simplified account list (uid and name).\n- GET /getAuthUrl: returns JSON {authUrl: ...} useful for client-side redirection.\n- GET /accounts/{id}/balance: returns account balances for the given account id.\n\nAuthentication: external provider-specific; this application generates client JWTs for the EnableBanking provider.\n\nExamples:\n1) Browser flow: call /login_to_bank -> user signs in at bank -> bank redirects to /callback with code.\n2) Server flow: call /getAuthUrl to fetch provider URL and redirect the user from browser/client.")
                        .contact(new Contact().name("Soletrack").email("dev@soletrack.local"))
                );
    }
}

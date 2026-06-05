package com.nllab.soletrack.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpenApiConfig {

    @Value("classpath:openapi-description.md")
    private Resource openApiDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        String description = "";
        try {
            description = openApiDescription.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            description = "Load resource openapi-description failed.";
        }
        return new OpenAPI()
                .info(new Info()
                        .title("Soletrack API")
                        .version("v1")
                        .description(description)
                        .contact(new Contact().name("Soletrack").email("dev@soletrack.local"))
                );
    }
}

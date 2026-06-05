package com.nllab.soletrack.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Amount and currency for a balance")
public record BalanceAmount(
        @JsonProperty("amount") @Schema(description = "Amount as string; can be negative") String amount,// "amount": "-134.49"
        @JsonProperty("currency") @Schema(description = "Currency code, e.g., EUR") String currency// "currency": "EUR"
) {}

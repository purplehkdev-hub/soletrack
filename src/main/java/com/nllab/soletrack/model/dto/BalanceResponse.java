package com.nllab.soletrack.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BalanceResponse(
        @JsonProperty("balances") List<BalanceItem> balances
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record BalanceItem(
        @JsonProperty("name") String name,
        @JsonProperty("balance_type") String balanceType, // "balance_type": "ITBD"
        @JsonProperty("balance_amount") BalanceAmount balanceAmount,
        @JsonProperty("reference_date") String referenceDate
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record BalanceAmount(
        @JsonProperty("amount") String amount,     // "amount": "-134.49"
        @JsonProperty("currency") String currency  // "currency": "EUR"
) {}
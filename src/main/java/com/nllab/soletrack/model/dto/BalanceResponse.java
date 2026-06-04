package com.nllab.soletrack.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Response containing a list of balances for an account")
public record BalanceResponse(
        @JsonProperty("balances") List<BalanceItem> balances
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "A single balance entry for an account")
record BalanceItem(
        @JsonProperty("name") @Schema(description = "Display name of the balance type") String name,
        @JsonProperty("balance_type") @Schema(description = "Type of balance, e.g., ITBD") String balanceType, // "balance_type": "ITBD"
        @JsonProperty("balance_amount") @Schema(description = "Amount and currency") BalanceAmount balanceAmount,
        @JsonProperty("reference_date") @Schema(description = "Reference date for the balance") String referenceDate
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Amount and currency for a balance")
record BalanceAmount(
        @JsonProperty("amount") @Schema(description = "Amount as string; can be negative") String amount,     // "amount": "-134.49"
        @JsonProperty("currency") @Schema(description = "Currency code, e.g., EUR") String currency  // "currency": "EUR"
) {}
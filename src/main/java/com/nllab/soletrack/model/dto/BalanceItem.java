package com.nllab.soletrack.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "A single balance entry for an account")
public record BalanceItem(
        @JsonProperty("name") @Schema(description = "Display name of the balance type") String name,
        @JsonProperty("balance_type") @Schema(description = "Type of balance, e.g., ITBD") String balanceType, // "balance_type": "ITBD"
        @JsonProperty("balance_amount") @Schema(description = "Amount and currency") BalanceAmount balanceAmount,
        @JsonProperty("reference_date") @Schema(description = "Reference date for the balance") String referenceDate
) {}

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


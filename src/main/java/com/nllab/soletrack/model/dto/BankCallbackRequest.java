package com.nllab.soletrack.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload model for bank callback containing the authorization code and optional state")
public record BankCallbackRequest(String code, String state) {}
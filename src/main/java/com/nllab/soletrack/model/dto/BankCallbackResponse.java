package com.nllab.soletrack.model.dto;

import java.util.List;

public record BankCallbackResponse(
        String status,
        String message,
        Integer totalAccountsFound,
        List<AccountDetail> accounts
) {
    public static BankCallbackResponse success(List<AccountDetail> accounts) {
        return new BankCallbackResponse("SUCCESS", null, accounts.size(), accounts);
    }

    public static BankCallbackResponse empty() {
        return new BankCallbackResponse("EMPTY", "No account exist.", 0, List.of());
    }

    public static BankCallbackResponse error(String message) {
        return new BankCallbackResponse("ERROR", message, null, null);
    }
}

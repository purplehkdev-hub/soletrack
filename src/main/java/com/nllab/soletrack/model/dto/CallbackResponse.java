package com.nllab.soletrack.model.dto;

import java.util.List;

public record CallbackResponse (
        String status,
        String message,
        String result
) {
    public static CallbackResponse success(String result) {
        return new CallbackResponse("SUCCESS", "", result);
    }

    public static CallbackResponse empty() {
        return new CallbackResponse("EMPTY", "No Record", "");
    }

    public static CallbackResponse error(String message) {
        return new CallbackResponse("ERROR", message, "");
    }
}

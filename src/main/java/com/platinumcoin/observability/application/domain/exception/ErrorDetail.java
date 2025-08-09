package com.platinumcoin.observability.application.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetail {
    private final String field;
    private final String message;
    private final Object rejectedValue;

    public String errorMessage() {
        return String.format("%s: %s (was %s)", field, message, rejectedValue);
    }
}
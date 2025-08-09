package com.platinumcoin.observability.application.domain.exception;

import java.util.Collections;
import java.util.List;

public class DomainException extends RuntimeException {
    private final String code;
    private final List<ErrorDetail> errors;

    public DomainException(String message, String code, List<ErrorDetail> errors) {
        super(message);
        this.code = code;
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public String getCode() {
        return code;
    }

    public List<com.platinumcoin.observability.application.domain.exception.ErrorDetail> getErrors() {
        return errors;
    }
}
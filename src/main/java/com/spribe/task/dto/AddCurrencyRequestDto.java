package com.spribe.task.dto;

import java.util.Objects;

public record AddCurrencyRequestDto(String source, String currency) {
    public AddCurrencyRequestDto(String source, String currency) {
        this.source = source;
        this.currency = Objects.requireNonNullElse(currency, "USD");
    }
}

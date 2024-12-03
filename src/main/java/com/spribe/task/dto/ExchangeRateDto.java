package com.spribe.task.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRateDto(Map<String, BigDecimal> exchangeRate) {
}

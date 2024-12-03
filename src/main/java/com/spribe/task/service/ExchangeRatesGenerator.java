package com.spribe.task.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface ExchangeRatesGenerator {
    Map<String, BigDecimal> generate(Collection<String> currencies);
}

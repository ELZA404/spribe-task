package com.spribe.task.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface ExchangeRatesService {
    Set<String> getCurrencies();
    BigDecimal getRate(String currency);
    void addCurrency(String currency);
    void updateRates(Map<String, BigDecimal> currencies);
}

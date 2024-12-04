package com.spribe.task.service.impl;

import com.spribe.task.service.ExchangeRatesService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {
    private final Map<String, BigDecimal> CURRENCY_RATES = new ConcurrentHashMap<>();

    @Override
    public BigDecimal getRate(String currency) {
        if (!CURRENCY_RATES.containsKey(currency)) {
            throw new RuntimeException("currency does not exist in project: %s".formatted(currency));
        }
        return CURRENCY_RATES.get(currency);
    }

    @Override
    public Set<String> getCurrencies() {
        return CURRENCY_RATES.keySet();
    }

    @Override
    public void addCurrency(String currency) {
        if (CURRENCY_RATES.containsKey(currency)) {
            throw new RuntimeException("currency already exists in project: %s".formatted(currency));
        }
        CURRENCY_RATES.put(currency, new BigDecimal(-1));
    }

    @Override
    public void updateRates(Map<String, BigDecimal> currencies) {
        CURRENCY_RATES.putAll(currencies);
    }
}

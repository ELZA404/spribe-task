package com.spribe.task.service.impl;

import com.spribe.task.service.ExchangeRatesGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangeRatesGeneratorImpl implements ExchangeRatesGenerator {
    @Override
    public Map<String, BigDecimal> generate(Collection<String> currencies) {
        return currencies.stream()
            .collect(Collectors.toMap(
                e -> e,
                e -> BigDecimal.valueOf(Math.random())));
    }
}

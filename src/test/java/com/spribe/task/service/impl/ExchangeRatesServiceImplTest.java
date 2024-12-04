package com.spribe.task.service.impl;

import com.spribe.task.service.ExchangeRatesService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRatesServiceImplTest {
    private ExchangeRatesService exchangeRatesService;

    @BeforeEach
    void setUp() {
        exchangeRatesService = new ExchangeRatesServiceImpl();
    }

    @Test
    void testGetCurrencies() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency = "UAHEUR";
        exchangeRatesService.addCurrency(testCurrency);

        Set<String> response = exchangeRatesService.getCurrencies();
        assertFalse(CollectionUtils.isEmpty(response));
        assertEquals(1, response.size());
        assertEquals(testCurrency, response.iterator().next());
    }

    @Test
    void testAddCurrencySuccess() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency1 = "UAHEUR";
        var testCurrency2 = "UAHUSD";

        assertThatNoException().isThrownBy(() -> exchangeRatesService.addCurrency(testCurrency1));
        assertThatNoException().isThrownBy(() -> exchangeRatesService.addCurrency(testCurrency2));

        Set<String> response = exchangeRatesService.getCurrencies();
        assertFalse(CollectionUtils.isEmpty(response));
        assertEquals(2, response.size());
        assertTrue(response.contains(testCurrency1));
        assertTrue(response.contains(testCurrency2));
    }

    @Test
    void testAddExistingCurrency() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency = "UAHUSD";

        assertThatNoException().isThrownBy(() -> exchangeRatesService.addCurrency(testCurrency));
        assertThatThrownBy(() -> exchangeRatesService.addCurrency(testCurrency))
            .isInstanceOf(RuntimeException.class)
            .extracting(RuntimeException.class::cast)
            .matches(ex -> StringUtils.equals(ex.getMessage(), "currency already exists in project: %s".formatted(testCurrency)));

        Set<String> response = exchangeRatesService.getCurrencies();
        assertFalse(CollectionUtils.isEmpty(response));
        assertEquals(1, response.size());
        assertEquals(testCurrency, response.iterator().next());
    }

    @Test
    void testGetRateSuccess() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency = "UAHEUR";
        var rate = BigDecimal.valueOf(1.4);

        exchangeRatesService.addCurrency(testCurrency);
        exchangeRatesService.updateRates(Map.of(testCurrency, rate));

        BigDecimal res = exchangeRatesService.getRate(testCurrency);
        assertNotNull(res);
        assertEquals(rate, res);
    }

    @Test
    void testGetRateNotExistingCurrency() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency = "UAHEUR";

        assertThatThrownBy(() -> exchangeRatesService.getRate(testCurrency))
            .isInstanceOf(RuntimeException.class)
            .extracting(RuntimeException.class::cast)
            .matches(ex -> StringUtils.equals(ex.getMessage(), "currency does not exist in project: %s".formatted(testCurrency)));
    }


    @Test
    void testUpdateRates() {
        assertTrue(CollectionUtils.isEmpty(exchangeRatesService.getCurrencies()));

        var testCurrency1 = "UAHUSD";
        var testCurrency2 = "UAHEUR";
        Map<String, BigDecimal> rates = new HashMap<>() {{
                put(testCurrency1, BigDecimal.valueOf(1.1));
                put(testCurrency2, BigDecimal.valueOf(1.3));
            }};
        exchangeRatesService.updateRates(rates);

        rates.put(testCurrency1, BigDecimal.valueOf(2.1));
        rates.put(testCurrency2, BigDecimal.valueOf(2.3));
        exchangeRatesService.updateRates(rates);

        BigDecimal res1 = exchangeRatesService.getRate(testCurrency1);
        assertNotNull(res1);
        assertEquals(rates.get(testCurrency1), res1);
        BigDecimal res2 = exchangeRatesService.getRate(testCurrency2);
        assertNotNull(res2);
        assertEquals(rates.get(testCurrency2), res2);
    }
}

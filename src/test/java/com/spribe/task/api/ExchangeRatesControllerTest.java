package com.spribe.task.api;

import com.spribe.task.dto.AddCurrencyRequestDto;
import com.spribe.task.dto.ExchangeRateResponseDto;
import com.spribe.task.model.ExchangeRatesEntity;
import com.spribe.task.repository.ExchangeRatesRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRatesControllerTest extends BaseTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ExchangeRatesRepository repository;

    private static final String BASE_URL = "http://localhost:%s/v1/exchange/rates";
    private static final String GET_CURRENCIES_URL = BASE_URL + "/currency/list";
    private static final String GET_EXCHANGE_RATE_URL = BASE_URL + "/currency/%s";
    private static final String CREATE_CURRENCY_URL = BASE_URL + "/currency";

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DirtiesContext
    void testAddNewCurrency() {
        ResponseEntity<Set<String>> getResponse = getCurrencies();
        assertTrue(CollectionUtils.isEmpty(getResponse.getBody()));

        var newCurrency = new AddCurrencyRequestDto("UAH", "EUR");
        ResponseEntity<Void> postResponse = postCurrency(newCurrency);

        ResponseEntity<Set<String>> getNewCurrenciesResponse = getCurrencies();
        Set<String> responseSet = getNewCurrenciesResponse.getBody();
        assertTrue(CollectionUtils.isNotEmpty(responseSet));
        assertEquals(1, responseSet.size());
        assertEquals(newCurrency.source() + newCurrency.currency(), responseSet.iterator().next());
    }

    @Test
    @DirtiesContext
    void testAddExistingCurrency() throws InterruptedException {
        ResponseEntity<Set<String>> getResponse = getCurrencies();
        assertTrue(CollectionUtils.isEmpty(getResponse.getBody()));

        var newCurrency = new AddCurrencyRequestDto("UAH", "EUR");
        postCurrency(newCurrency, HttpStatus.OK);
        postCurrency(newCurrency, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DirtiesContext
    void testGetNotExistingExchangeRates() {
        ResponseEntity<Set<String>> getResponse = getCurrencies();
        assertTrue(CollectionUtils.isEmpty(getResponse.getBody()));

        getExchangeRate("UAH", "EUR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DirtiesContext
    void testExchangeRatesAreGeneratedAndSaved() throws InterruptedException {
        ResponseEntity<Set<String>> getResponse = getCurrencies();
        assertTrue(CollectionUtils.isEmpty(getResponse.getBody()));

        var newCurrency = new AddCurrencyRequestDto("UAH", "EUR");
        postCurrency(newCurrency);

        Thread.sleep(6000);

        List<ExchangeRatesEntity> ratesFromDb = repository.findAll();
        assertTrue(CollectionUtils.isNotEmpty(ratesFromDb));
        assertEquals(1, ratesFromDb.size());
        ResponseEntity<ExchangeRateResponseDto> ratesResponseEntity = getExchangeRate(newCurrency.source(), newCurrency.currency());
        ExchangeRateResponseDto rateResponseDto = ratesResponseEntity.getBody();
        assertNotNull(rateResponseDto);
        assertTrue(MapUtils.isNotEmpty(rateResponseDto.exchangeRate()));
        assertEquals(1, rateResponseDto.exchangeRate().size());

        ExchangeRatesEntity rateFromDb = ratesFromDb.get(0);
        Map.Entry<String, BigDecimal> rateResponseEntry = rateResponseDto.exchangeRate().entrySet().iterator().next();
        assertEquals(rateFromDb.getCurrency(), rateResponseEntry.getKey());
        assertEquals(rateFromDb.getRate(), rateResponseEntry.getValue());
        assertNotNull(rateFromDb.getId());
        assertNotNull(rateFromDb.getCreatedAt());

    }

    private ResponseEntity<Set<String>> getCurrencies() {
        return getCurrencies(HttpStatus.OK);
    }

    private ResponseEntity<Set<String>> getCurrencies(HttpStatus expectedResponseCode) {
        ResponseEntity<Set<String>> response = restTemplate.exchange(GET_CURRENCIES_URL.formatted(port), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        assertEquals(expectedResponseCode, response.getStatusCode());
        assertNotNull(response.getBody());
        return response;
    }

    private ResponseEntity<ExchangeRateResponseDto> getExchangeRate(String source, String currency) {
        return getExchangeRate(source, currency, HttpStatus.OK);
    }

    private ResponseEntity<ExchangeRateResponseDto> getExchangeRate(String source, String currency, HttpStatus expectedResponseCode) {
        String url = GET_EXCHANGE_RATE_URL.formatted(port, source);
        if (currency != null) {
            url = url + "?currency=" + currency;
        }
        ResponseEntity<ExchangeRateResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, null, ExchangeRateResponseDto.class);
        assertEquals(expectedResponseCode, response.getStatusCode());
        assertNotNull(response.getBody());
        return response;
    }


    private ResponseEntity<Void> postCurrency(AddCurrencyRequestDto request) {
        return postCurrency(request, HttpStatus.OK);
    }

    private ResponseEntity<Void> postCurrency(AddCurrencyRequestDto request, HttpStatus expectedResponseCode) {
        ResponseEntity<Void> response = restTemplate.postForEntity(CREATE_CURRENCY_URL.formatted(port), request, Void.class);
        assertEquals(expectedResponseCode, response.getStatusCode());
        return response;
    }
}

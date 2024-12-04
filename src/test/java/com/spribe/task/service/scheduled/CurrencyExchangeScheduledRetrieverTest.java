package com.spribe.task.service.scheduled;

import com.spribe.task.model.ExchangeRatesEntity;
import com.spribe.task.repository.ExchangeRatesRepository;
import com.spribe.task.scheduled.CurrencyExchangeScheduledRetriever;
import com.spribe.task.service.ExchangeRatesGenerator;
import com.spribe.task.service.ExchangeRatesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeScheduledRetrieverTest {
    @Mock
    private ExchangeRatesService exchangeRatesService;
    @Mock
    private ExchangeRatesRepository exchangeRatesRepository;
    @Mock
    private ExchangeRatesGenerator exchangeRatesGenerator;

    @InjectMocks
    private CurrencyExchangeScheduledRetriever retriever;

    @Captor
    private ArgumentCaptor<Set<String>> currenciesCaptor;
    @Captor
    private ArgumentCaptor<Iterable<ExchangeRatesEntity>> exchangeRatesEntitiesCaptor;

    @Test
    void testSuccess() {
        var currency1 = "UAHEUR";
        var currency2 = "UAHUSD";
        var currencies = Set.of(currency1, currency2);
        when(exchangeRatesService.getCurrencies()).thenReturn(currencies);
        var exchangeRates = Map.of(
            currency1, BigDecimal.valueOf(1.1),
            currency2, BigDecimal.valueOf(2.2));
        when(exchangeRatesGenerator.generate(anySet())).thenReturn(exchangeRates);
        when(exchangeRatesRepository.saveAll(anyCollection())).thenReturn(List.of());

        retriever.getExchangeRates();

        verify(exchangeRatesService).getCurrencies();
        verify(exchangeRatesGenerator).generate(currenciesCaptor.capture());
        assertEquals(currencies, currenciesCaptor.getValue());
        verify(exchangeRatesRepository).saveAll(exchangeRatesEntitiesCaptor.capture());
        List<ExchangeRatesEntity> savedEntities = StreamSupport.stream(exchangeRatesEntitiesCaptor.getValue().spliterator(), false).toList();

        assertTrue(savedEntities.stream().map(ExchangeRatesEntity::getId).noneMatch(Objects::isNull));
        Map<String, BigDecimal> savedRates = savedEntities.stream()
            .collect(Collectors.toMap(ExchangeRatesEntity::getCurrency, ExchangeRatesEntity::getRate));
        assertEquals(exchangeRates, savedRates);
    }

    @Test
    void testEmptyCurrencyList() {
        when(exchangeRatesService.getCurrencies()).thenReturn(Set.of());

        retriever.getExchangeRates();

        verify(exchangeRatesGenerator, never()).generate(anyCollection());
        verify(exchangeRatesService, never()).updateRates(anyMap());
        verify(exchangeRatesRepository, never()).saveAll(anyCollection());
    }
}

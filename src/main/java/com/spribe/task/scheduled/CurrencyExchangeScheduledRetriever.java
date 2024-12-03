package com.spribe.task.scheduled;

import com.spribe.task.model.ExchangeRatesEntity;
import com.spribe.task.repository.ExchangeRatesRepository;
import com.spribe.task.service.ExchangeRatesGenerator;
import com.spribe.task.service.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeScheduledRetriever {

    private final ExchangeRatesService exchangeRatesService;
    private final ExchangeRatesRepository exchangeRatesRepository;
    private final ExchangeRatesGenerator exchangeRatesGenerator;

    @Async
    @Scheduled(fixedRateString = "${scheduler.interval}", timeUnit = TimeUnit.SECONDS)
    public void getExchangeRates() {
        Set<String> currencies = exchangeRatesService.getCurrencies();
        if (CollectionUtils.isEmpty(currencies)) {
            return;
        }
        Map<String, BigDecimal> exchangeRates = exchangeRatesGenerator.generate(currencies);

        exchangeRatesService.updateRates(exchangeRates);
        List<ExchangeRatesEntity> exchangeRatesEntities = exchangeRates.entrySet().stream()
            .map(e -> {
                var entity = new ExchangeRatesEntity();
                entity.setId(UUID.randomUUID());
                entity.setCurrency(e.getKey());
                entity.setRate(e.getValue());
                return entity;
            }).toList();
        exchangeRatesRepository.saveAll(exchangeRatesEntities);
    }
}

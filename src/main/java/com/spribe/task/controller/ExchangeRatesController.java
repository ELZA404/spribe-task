package com.spribe.task.controller;

import com.spribe.task.dto.ExchangeRateDto;
import com.spribe.task.service.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/exchange/rates")
@RequiredArgsConstructor
public class ExchangeRatesController {
    private final ExchangeRatesService exchangeRatesService;

    @GetMapping("/currency/list")
    public ResponseEntity<Set<String>> getCurrencies() {
        return ResponseEntity.ok(exchangeRatesService.getCurrencies());
    }

    @GetMapping("/currency/{source}")
    public ResponseEntity<ExchangeRateDto> getExchangeRate(@PathVariable(name = "source") String source,
                                                           @RequestParam(name = "currency", required = false, defaultValue = "USD") String currency) {
        String sourceToCurrency = source + currency;
        BigDecimal rate = exchangeRatesService.getRate(sourceToCurrency.toUpperCase());

        return ResponseEntity.ok(new ExchangeRateDto(Map.of(sourceToCurrency, rate)));
    }

    @PostMapping("/currency/{source}")
    public ResponseEntity<Void> addCurrency(@PathVariable(name = "source") String source,
                                            @RequestParam(name = "currency", required = false, defaultValue = "USD") String currency) {
        exchangeRatesService.addCurrency((source + currency).toUpperCase());
        return ResponseEntity.ok().build();
    }
}

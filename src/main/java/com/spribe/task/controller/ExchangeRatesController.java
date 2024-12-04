package com.spribe.task.controller;

import com.spribe.task.dto.AddCurrencyRequestDto;
import com.spribe.task.dto.ExchangeRateResponseDto;
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
    public ResponseEntity<ExchangeRateResponseDto> getExchangeRate(@PathVariable(name = "source") String source,
                                                                   @RequestParam(name = "currency", required = false, defaultValue = "USD") String currency) {
        String sourceToCurrency = source + currency;
        BigDecimal rate = exchangeRatesService.getRate(sourceToCurrency.toUpperCase());

        return ResponseEntity.ok(new ExchangeRateResponseDto(Map.of(sourceToCurrency, rate)));
    }

    @PostMapping("/currency")
    public ResponseEntity<Void> addCurrency(@RequestBody AddCurrencyRequestDto addCurrencyRequestDto) {
        String sourceToCurrency = addCurrencyRequestDto.source() + addCurrencyRequestDto.currency();
        exchangeRatesService.addCurrency(sourceToCurrency.toUpperCase());
        return ResponseEntity.ok().build();
    }
}

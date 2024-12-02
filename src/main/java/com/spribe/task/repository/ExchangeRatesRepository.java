package com.spribe.task.repository;

import com.spribe.task.model.ExchangeRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<ExchangeRates, UUID> {
}

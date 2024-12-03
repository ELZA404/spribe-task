package com.spribe.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "exchange_rates")
public class ExchangeRatesEntity {
    @Id
    @Column
    private UUID id;

    @Column
    private String currency;

    @Column
    private BigDecimal rate;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}

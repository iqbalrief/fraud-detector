package com.example.fraud.detector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private Long accountId;
    private Long merchantId;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
    private String fraudStatus;
    private BigDecimal  fraudScore;
}


package com.example.fraud.detector.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    private Long accountId;
    private Long merchantId;
    private BigDecimal amount;
}

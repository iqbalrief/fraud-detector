package com.example.fraud.detector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FraudResultDto {
    Long transactionId;
    int fraudScore;
    String fraudStatus;
}


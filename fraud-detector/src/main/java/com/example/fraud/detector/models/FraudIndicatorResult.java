package com.example.fraud.detector.models;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FraudIndicatorResult {
    String indicator;
    int score;
    boolean triggered;
}

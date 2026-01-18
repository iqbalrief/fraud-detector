package com.example.fraud.detector.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fraud_results")
public class FraudResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private Integer fraudScore;

    private String fraudStatus;

}

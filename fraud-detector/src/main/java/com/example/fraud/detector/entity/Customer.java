package com.example.fraud.detector.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
}

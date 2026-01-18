package com.example.fraud.detector.repository;

import com.example.fraud.detector.entity.FraudResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FraudResultRepository extends JpaRepository<FraudResult, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO fraud_results (fraud_score, fraud_status, transaction_id)
        VALUES (:score, :status, :transactionId)
    """, nativeQuery = true)
    void insertFraudResult(
            @Param("score") BigDecimal score,
            @Param("status") String status,
            @Param("transactionId") Long transactionId
    );


    @Query("""
        SELECT fr
        FROM FraudResult fr
        JOIN FETCH fr.transaction t
        JOIN FETCH t.account a
        JOIN FETCH a.customer c
        JOIN FETCH c.user u
        JOIN FETCH t.merchant m
    """)
    List<FraudResult> findAllWithDetail();
}

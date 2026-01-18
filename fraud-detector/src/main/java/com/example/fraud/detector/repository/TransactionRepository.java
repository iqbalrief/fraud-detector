package com.example.fraud.detector.repository;
import com.example.fraud.detector.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    // FREQUENT_TRANSACTION
    @Query(value = """
    SELECT COUNT(*) 
    FROM transactions 
    WHERE account_id = :accountId
      AND transaction_time >= NOW() - INTERVAL '5 minutes'
  """, nativeQuery = true)
    int countRecentTransaction(
            @Param("accountId") Long accountId
    );

    // SAME_AMOUNT_REPEAT
    @Query(value = """
    SELECT COUNT(*) 
    FROM transactions 
    WHERE account_id = :accountId
      AND amount = :amount
      AND transaction_time >= NOW() - INTERVAL '1 day'
  """, nativeQuery = true)
    int countSameAmount(
            @Param("accountId") Long accountId,
            @Param("amount") Double amount
    );


    @Query(value = """
    SELECT COUNT(*) 
    FROM transactions 
    WHERE account_id = :accountId
      AND merchant_id = :merchantId
  """, nativeQuery = true)
    int countMerchantUsage(
            @Param("accountId") Long accountId,
            @Param("merchantId") Long merchantId
    );

    @Query(value = """
    SELECT AVG(amount)
    FROM transactions
    WHERE account_id = :accountId
    """, nativeQuery = true)
    BigDecimal getAverageAmount(@Param("accountId") Long accountId
    );

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO transactions (account_id, merchant_id, amount, transaction_time, created_at, updated_at) " +
                    "VALUES (:accountId, :merchantId, :amount, :transactionTime, NOW(), NOW())",
            nativeQuery = true
    )
    Long insertTransaction(
            @Param("accountId") Long accountId,
            @Param("merchantId") Long merchantId,
            @Param("amount") BigDecimal amount,
            @Param("transactionTime") LocalDateTime LocalDateTime
    );


    @Query(value = """
        SELECT t.transaction_id, t.account_id, t.merchant_id, t.amount, t.transaction_time,
               f.fraud_status, f.fraud_score
        FROM transactions t
        LEFT JOIN fraud_results f ON t.transaction_id = f.transaction_id
    """, nativeQuery = true)
    List<Object[]> findAllTransactionsWithFraud();
}

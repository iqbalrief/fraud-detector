package com.example.fraud.detector.repository;

import com.example.fraud.detector.entity.Account;

import com.example.fraud.detector.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM accounts", nativeQuery = true)
    List<Transaction> findAllAcountsNative();

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO accounts (customer_id, balance, last_active_at)
        VALUES (:customerId, :balance, CURRENT_TIMESTAMP)
    """, nativeQuery = true)
    void insertAccount(
            @Param("customerId") Long customerId,
            @Param("balance") BigDecimal balance,
            @Param("lastActiveAt") LocalDateTime lastActiveAt

    );

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE accounts
    SET balance = balance - :amount,
        last_active_at = CURRENT_TIMESTAMP
    WHERE id = :accountId
""", nativeQuery = true)
    int deductBalance(
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );

    @Query(value = """
        SELECT COUNT(*) 
        FROM accounts a
        JOIN customers c ON a.customer_id = c.id
        WHERE a.id = :accountId AND c.id = :userId
    """, nativeQuery = true)
    int countAccountBelongsToUser(@Param("accountId") Long accountId, @Param("userId") Long userId);
}

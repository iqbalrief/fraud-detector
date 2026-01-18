package com.example.fraud.detector.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface DataSeederRepository {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO merchants (name) VALUES (:name)", nativeQuery = true)
    void insertMerchant(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO users (username, api_key)
        VALUES (:username, gen_random_uuid())
    """, nativeQuery = true)
    void insertUser(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO customers (name, user_id)
        VALUES (:name, :userId)
    """, nativeQuery = true)
    void insertCustomer(
            @Param("name") String name,
            @Param("userId") Long userId
    );

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO accounts (customer_id, balance)
        VALUES (:customerId, :balance)
    """, nativeQuery = true)
    void insertAccount(
            @Param("customerId") Long customerId,
            @Param("balance") BigDecimal balance
    );

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO transactions (account_id, merchant_id, amount, transaction_time)
        VALUES (:accountId, :merchantId, :amount, NOW())
    """, nativeQuery = true)
    void insertTransaction(
            @Param("accountId") Long accountId,
            @Param("merchantId") Long merchantId,
            @Param("amount") BigDecimal amount
    );
}

package com.example.fraud.detector.repository;

import com.example.fraud.detector.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
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
}

package com.example.fraud.detector.repository;

import com.example.fraud.detector.entity.Merchant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO merchants (name,category) VALUES (:name,:category)", nativeQuery = true)
    void insertMerchant(@Param("name") String name, @Param("category") String category );

}

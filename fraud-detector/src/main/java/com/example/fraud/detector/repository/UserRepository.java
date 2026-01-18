package com.example.fraud.detector.repository;


import com.example.fraud.detector.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO users (username, api_key)
        VALUES (:username, gen_random_uuid())
    """, nativeQuery = true)
    void insertUser(@Param("username") String username);

    @Query(value = """
        SELECT u.id
        FROM users u
        WHERE u.api_key = :apiKey
        """, nativeQuery = true)
    Long findByApiKey(@Param("apiKey") String apiKey);

}

package com.innowise.academy.user_service.repository;

import com.innowise.academy.user_service.entity.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, Long>,
        JpaSpecificationExecutor<PaymentCardEntity> {

    List<PaymentCardEntity> findByUser_Id(Long userId);

    @Query("""
            SELECT c
            FROM PaymentCardEntity c
            WHERE c.user.id = :userId
              AND c.active = true
            """)
    List<PaymentCardEntity> findActiveCardsByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT COUNT(*)
            FROM payment_cards
            WHERE user_id = :userId
              AND active = true
            """, nativeQuery = true)
    long countActiveCardsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PaymentCardEntity c SET c.active = :active WHERE c.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}
package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para OrderEntity con Query Methods
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByUserId(Long userId);
    
    List<OrderEntity> findByStatus(String status);
    
    List<OrderEntity> findByUserIdAndStatus(Long userId, String status);
    
    List<OrderEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Query con JOIN FETCH para cargar relaciones
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.user WHERE o.id = :orderId")
    OrderEntity findByIdWithUser(@Param("orderId") Long orderId);
    
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderItems WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserIdWithItems(@Param("userId") Long userId);
}

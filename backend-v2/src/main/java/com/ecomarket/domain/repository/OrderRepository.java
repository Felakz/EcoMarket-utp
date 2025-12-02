package com.ecomarket.domain.repository;

import com.ecomarket.domain.model.Order;
import com.ecomarket.domain.model.Order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para Order
 */
public interface OrderRepository {
    
    Order save(Order order);
    
    Optional<Order> findById(Long id);
    
    List<Order> findAll();
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    void deleteById(Long id);
}

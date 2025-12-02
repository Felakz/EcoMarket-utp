package com.ecomarket.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Domain Entity - Representa una orden de compra
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    private Long id;
    private User user;
    
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String trackingNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Enum para estados de orden
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    // Métodos de negocio
    
    public void addItem(OrderItem item) {
        this.orderItems.add(item);
        recalculateTotal();
    }
    
    public void removeItem(OrderItem item) {
        this.orderItems.remove(item);
        recalculateTotal();
    }
    
    public void recalculateTotal() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void process() {
        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void ship(String trackingNumber) {
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deliver() {
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }
    
    public boolean isDelivered() {
        return this.status == OrderStatus.DELIVERED;
    }
}

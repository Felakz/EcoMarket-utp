package com.ecomarket.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Order items are required")
    private List<OrderItemRequest> orderItems;
    
    @NotNull(message = "Shipping address is required")
    private String shippingAddress;
    
    private String billingAddress;
    private String paymentMethod;
    private String notes;
    
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }
}

package com.ecomarket.presentation.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mensaje para notificaciones en tiempo real vía WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    
    private String type;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
    
    public static NotificationMessage create(String type, String message, Object data) {
        return new NotificationMessage(type, message, data, LocalDateTime.now());
    }
    
    // Tipos de notificación
    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_UPDATED = "ORDER_UPDATED";
    public static final String PRODUCT_STOCK_LOW = "PRODUCT_STOCK_LOW";
    public static final String NEW_PRODUCT = "NEW_PRODUCT";
}

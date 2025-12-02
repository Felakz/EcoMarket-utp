package com.ecomarket.presentation.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar notificaciones en tiempo real vía WebSocket
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Envía notificación a todos los usuarios suscritos a un topic
     */
    public void sendNotificationToTopic(String topic, NotificationMessage message) {
        log.info("Sending notification to topic {}: {}", topic, message.getType());
        messagingTemplate.convertAndSend("/topic/" + topic, message);
    }
    
    /**
     * Envía notificación a un usuario específico
     */
    public void sendNotificationToUser(String username, NotificationMessage message) {
        log.info("Sending notification to user {}: {}", username, message.getType());
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }
    
    /**
     * Notifica cuando se crea una nueva orden
     */
    public void notifyOrderCreated(Long orderId, String username) {
        NotificationMessage message = NotificationMessage.create(
            NotificationMessage.ORDER_CREATED,
            "New order created",
            orderId
        );
        
        // Notificar al usuario
        sendNotificationToUser(username, message);
        
        // Notificar a admins
        sendNotificationToTopic("admin/orders", message);
    }
    
    /**
     * Notifica cuando se actualiza el estado de una orden
     */
    public void notifyOrderUpdated(Long orderId, String status, String username) {
        NotificationMessage message = NotificationMessage.create(
            NotificationMessage.ORDER_UPDATED,
            "Order status updated to: " + status,
            orderId
        );
        
        sendNotificationToUser(username, message);
    }
    
    /**
     * Notifica cuando el stock de un producto está bajo
     */
    public void notifyLowStock(Long productId, String productName, Integer stock) {
        NotificationMessage message = NotificationMessage.create(
            NotificationMessage.PRODUCT_STOCK_LOW,
            "Low stock alert for: " + productName,
            new Object() {
                public final Long id = productId;
                public final String name = productName;
                public final Integer currentStock = stock;
            }
        );
        
        sendNotificationToTopic("admin/inventory", message);
    }
    
    /**
     * Notifica cuando se agrega un nuevo producto
     */
    public void notifyNewProduct(Long productId, String productName) {
        NotificationMessage message = NotificationMessage.create(
            NotificationMessage.NEW_PRODUCT,
            "New product available: " + productName,
            productId
        );
        
        sendNotificationToTopic("products", message);
    }
}

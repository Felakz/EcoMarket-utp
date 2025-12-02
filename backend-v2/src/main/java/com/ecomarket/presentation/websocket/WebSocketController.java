package com.ecomarket.presentation.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

/**
 * Controller WebSocket para mensajes en tiempo real
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final WebSocketNotificationService notificationService;
    
    /**
     * Endpoint para mensajes de chat o notificaciones generales
     * Cliente envía a: /app/message
     * Servidor broadcast a: /topic/messages
     */
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public NotificationMessage sendMessage(@Payload NotificationMessage message, 
                                          @AuthenticationPrincipal UserDetails user) {
        log.info("Received message from user {}: {}", user.getUsername(), message.getMessage());
        return message;
    }
    
    /**
     * Endpoint para notificaciones privadas
     * Cliente envía a: /app/private
     * Servidor envía a: /user/{username}/queue/private
     */
    @MessageMapping("/private")
    @SendToUser("/queue/private")
    public NotificationMessage sendPrivateMessage(@Payload NotificationMessage message,
                                                 @AuthenticationPrincipal UserDetails user) {
        log.info("Received private message from user {}", user.getUsername());
        return message;
    }
    
    /**
     * Endpoint para tracking de órdenes en tiempo real
     */
    @MessageMapping("/order/track")
    @SendToUser("/queue/order-updates")
    public NotificationMessage trackOrder(@Payload Long orderId,
                                         @AuthenticationPrincipal UserDetails user) {
        log.info("User {} tracking order {}", user.getUsername(), orderId);
        return NotificationMessage.create(
            "ORDER_TRACKING",
            "Order tracking enabled",
            orderId
        );
    }
}

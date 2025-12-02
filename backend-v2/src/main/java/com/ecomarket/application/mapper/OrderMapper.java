package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.OrderResponse;
import com.ecomarket.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper para Order
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(target = "userId", expression = "java(getUserId(order))")
    @Mapping(target = "username", expression = "java(getUsername(order))")
    @Mapping(target = "status", expression = "java(getStatusString(order))")
    OrderResponse toResponse(Order order);
    
    List<OrderResponse> toResponseList(List<Order> orders);
    
    default Long getUserId(Order order) {
        return order.getUser() != null ? order.getUser().getId() : null;
    }
    
    default String getUsername(Order order) {
        return order.getUser() != null ? order.getUser().getUsername() : null;
    }
    
    default String getStatusString(Order order) {
        return order.getStatus() != null ? order.getStatus().name() : null;
    }
}

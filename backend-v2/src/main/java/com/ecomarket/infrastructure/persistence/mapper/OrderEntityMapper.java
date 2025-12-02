package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Order;
import com.ecomarket.infrastructure.persistence.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * Mapper entre OrderEntity y Order
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, 
        uses = {UserEntityMapper.class, OrderItemEntityMapper.class})
public interface OrderEntityMapper {
    
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    Order toDomain(OrderEntity entity);
    
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    OrderEntity toEntity(Order domain);
    
    @Named("stringToStatus")
    default Order.OrderStatus stringToStatus(String status) {
        return status != null ? Order.OrderStatus.valueOf(status) : null;
    }
    
    @Named("statusToString")
    default String statusToString(Order.OrderStatus status) {
        return status != null ? status.name() : null;
    }
}

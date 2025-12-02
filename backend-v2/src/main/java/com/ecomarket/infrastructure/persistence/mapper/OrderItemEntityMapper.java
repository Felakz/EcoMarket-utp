package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.OrderItem;
import com.ecomarket.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre OrderItemEntity y OrderItem
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, 
        uses = {ProductEntityMapper.class})
public interface OrderItemEntityMapper {
    
    OrderItem toDomain(OrderItemEntity entity);
    
    OrderItemEntity toEntity(OrderItem domain);
}

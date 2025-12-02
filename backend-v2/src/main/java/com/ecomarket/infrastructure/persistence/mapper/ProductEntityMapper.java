package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Product;
import com.ecomarket.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre ProductEntity y Product
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryEntityMapper.class})
public interface ProductEntityMapper {
    
    Product toDomain(ProductEntity entity);
    
    ProductEntity toEntity(Product domain);
}

package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Category;
import com.ecomarket.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre CategoryEntity y Category
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryEntityMapper {
    
    Category toDomain(CategoryEntity entity);
    
    CategoryEntity toEntity(Category domain);
}

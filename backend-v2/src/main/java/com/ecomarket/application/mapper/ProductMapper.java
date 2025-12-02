package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.ProductResponse;
import com.ecomarket.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para Product con mapeos personalizados
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);
    
    List<ProductResponse> toResponseList(List<Product> products);
}

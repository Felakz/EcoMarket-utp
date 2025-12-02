package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.CategoryResponse;
import com.ecomarket.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para Category
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    
    CategoryResponse toResponse(Category category);
    
    List<CategoryResponse> toResponseList(List<Category> categories);
}

package com.ecomarket.application.usecase;

import com.ecomarket.application.dto.response.CategoryResponse;
import com.ecomarket.application.mapper.CategoryMapper;
import com.ecomarket.domain.exception.EntityNotFoundException;
import com.ecomarket.domain.model.Category;
import com.ecomarket.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación para categorías
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Getting all categories");
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categoryMapper.toResponseList(categories);
    }
    
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.info("Getting category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }
}

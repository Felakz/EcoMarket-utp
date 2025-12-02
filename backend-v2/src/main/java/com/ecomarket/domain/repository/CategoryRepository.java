package com.ecomarket.domain.repository;

import com.ecomarket.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para Category
 */
public interface CategoryRepository {
    
    Category save(Category category);
    
    Optional<Category> findById(Long id);
    
    List<Category> findAll();
    
    List<Category> findByActiveTrue();
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
    
    void deleteById(Long id);
}

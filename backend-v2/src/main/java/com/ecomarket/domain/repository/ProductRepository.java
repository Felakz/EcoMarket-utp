package com.ecomarket.domain.repository;

import com.ecomarket.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para Product
 * Define Query Methods personalizados
 */
public interface ProductRepository {
    
    Product save(Product product);
    
    Optional<Product> findById(Long id);
    
    List<Product> findAll();
    
    List<Product> findByActiveTrue();
    
    List<Product> findByFeaturedTrue();
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId, Boolean active);
    
    List<Product> findByNameContaining(String name);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByIsOrganic(Boolean isOrganic);
    
    List<Product> findByOriginCountry(String country);
    
    List<Product> findByStockQuantityGreaterThan(Integer stock);
    
    List<Product> findByActiveTrueOrderByCreatedAtDesc();
    
    List<Product> findTop10ByActiveTrueOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
    
    void deleteById(Long id);
}

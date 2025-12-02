package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio JPA para ProductEntity con Query Methods personalizados
 */
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
    
    // Query Methods derivados
    List<ProductEntity> findByIsActiveTrue();
    
    List<ProductEntity> findByIsFeaturedTrue();
    
    List<ProductEntity> findByCategoryId(Long categoryId);
    
    List<ProductEntity> findByCategoryIdAndIsActive(Long categoryId, Boolean active);
    
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<ProductEntity> findByIsOrganic(Boolean isOrganic);
    
    List<ProductEntity> findByOriginCountry(String country);
    
    List<ProductEntity> findByStockQuantityGreaterThan(Integer stock);
    
    List<ProductEntity> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<ProductEntity> findTop10ByIsActiveTrueOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
    
    // Query personalizado con JOIN FETCH para evitar N+1
    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.category WHERE p.isActive = true")
    List<ProductEntity> findAllActiveWithCategory();
    
    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.category WHERE p.category.id = :categoryId AND p.isActive = true")
    List<ProductEntity> findActiveByCategoryWithCategory(@Param("categoryId") Long categoryId);
}

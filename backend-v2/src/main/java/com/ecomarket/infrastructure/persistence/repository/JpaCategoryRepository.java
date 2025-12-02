package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para CategoryEntity con Query Methods
 */
@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    List<CategoryEntity> findByIsActiveTrue();
    
    Optional<CategoryEntity> findByName(String name);
    
    boolean existsByName(String name);
}

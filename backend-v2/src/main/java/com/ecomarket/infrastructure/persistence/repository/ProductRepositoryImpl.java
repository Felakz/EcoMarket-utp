package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.domain.model.Product;
import com.ecomarket.domain.repository.ProductRepository;
import com.ecomarket.infrastructure.persistence.mapper.ProductEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de dominio Product usando JPA
 */
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    
    private final JpaProductRepository jpaRepository;
    private final ProductEntityMapper mapper;
    
    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByActiveTrue() {
        return jpaRepository.findAllActiveWithCategory().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByFeaturedTrue() {
        return jpaRepository.findByIsFeaturedTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryIdAndActiveTrue(Long categoryId, Boolean active) {
        return jpaRepository.findActiveByCategoryWithCategory(categoryId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return jpaRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByIsOrganic(Boolean isOrganic) {
        return jpaRepository.findByIsOrganic(isOrganic).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByOriginCountry(String country) {
        return jpaRepository.findByOriginCountry(country).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByStockQuantityGreaterThan(Integer stock) {
        return jpaRepository.findByStockQuantityGreaterThan(stock).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByActiveTrueOrderByCreatedAtDesc() {
        return jpaRepository.findByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findTop10ByActiveTrueOrderByCreatedAtDesc() {
        return jpaRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

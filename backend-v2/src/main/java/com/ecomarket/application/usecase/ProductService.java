package com.ecomarket.application.usecase;

import com.ecomarket.application.dto.response.ProductResponse;
import com.ecomarket.application.mapper.ProductMapper;
import com.ecomarket.domain.exception.EntityNotFoundException;
import com.ecomarket.domain.model.Product;
import com.ecomarket.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de aplicación para productos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products");
        List<Product> products = productRepository.findByActiveTrue();
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Getting product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.info("Getting products by category: {}", categoryId);
        List<Product> products = productRepository.findByCategoryIdAndActiveTrue(categoryId, true);
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts() {
        log.info("Getting featured products");
        List<Product> products = productRepository.findByFeaturedTrue();
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        List<Product> products = productRepository.findByNameContaining(keyword);
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Getting products by price range: {} - {}", minPrice, maxPrice);
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getOrganicProducts() {
        log.info("Getting organic products");
        List<Product> products = productRepository.findByIsOrganic(true);
        return productMapper.toResponseList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getLatestProducts() {
        log.info("Getting latest products");
        List<Product> products = productRepository.findTop10ByActiveTrueOrderByCreatedAtDesc();
        return productMapper.toResponseList(products);
    }
}

package com.ecomarket.domain.model;

import com.ecomarket.domain.exception.InsufficientStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Domain Entity - Representa un producto en el dominio
 * Contiene lógica de negocio relacionada con productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Category category;
    private String imageFilename;
    private Boolean isOrganic;
    private String certifications;
    private String originCountry;
    private BigDecimal carbonFootprint;
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Métodos de negocio del dominio
    
    public void decreaseStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                    this.name, this.stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void increaseStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isInStock() {
        return this.stockQuantity != null && this.stockQuantity > 0;
    }
    
    public boolean hasStock(Integer quantity) {
        return this.stockQuantity != null && this.stockQuantity >= quantity;
    }
    
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public void setAsFeatured() {
        this.isFeatured = true;
    }
    
    public void removeFromFeatured() {
        this.isFeatured = false;
    }
    
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal calculateTotalPrice(Integer quantity) {
        return this.price.multiply(BigDecimal.valueOf(quantity));
    }
}

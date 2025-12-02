package com.ecomarket.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String imageFilename;
    private Boolean isOrganic;
    private String certifications;
    private String originCountry;
    private BigDecimal carbonFootprint;
    private Boolean isActive;
    private Boolean isFeatured;
}

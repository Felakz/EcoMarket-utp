package com.ecomarket.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecomarket.category.Category;
import com.ecomarket.category.CategoryRepository;
import com.ecomarket.product.dto.ProductRequest;
import com.ecomarket.product.dto.ProductResponse;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    public ProductResponse getById(Long id) {
        return repository.findById(id).map(this::toDto).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public ProductResponse create(ProductRequest req) {
        Category cat = categoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock() != null ? req.getStock() : 0);
        p.setCategory(cat);
        p.setImageFilename(req.getImageFilename());
        p.setIsOrganic(req.getIsOrganic() != null ? req.getIsOrganic() : false);
        p.setCertifications(req.getCertifications());
        p.setOriginCountry(req.getOriginCountry());
        Product saved = repository.save(p);
        return toDto(saved);
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        Category cat = categoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock() != null ? req.getStock() : p.getStock());
        p.setCategory(cat);
        p.setImageFilename(req.getImageFilename());
        if (req.getIsOrganic() != null) p.setIsOrganic(req.getIsOrganic());
        p.setCertifications(req.getCertifications());
        p.setOriginCountry(req.getOriginCountry());
        Product saved = repository.save(p);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Product not found");
        repository.deleteById(id);
    }

    private ProductResponse toDto(Product p) {
        String imageUrl = null;
        if (p.getImageFilename() != null && !p.getImageFilename().isBlank()) {
            imageUrl = "/ecomarket/api/images/" + p.getImageFilename();
        }
        Long catId = p.getCategory() != null ? p.getCategory().getId() : null;
        String catName = p.getCategory() != null ? p.getCategory().getName() : null;
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), catId, catName, p.getImageFilename(), imageUrl, p.getIsOrganic(), p.getCertifications(), p.getOriginCountry(), p.getCreatedAt(), p.getUpdatedAt());
    }
}

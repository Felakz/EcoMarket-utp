package com.ecomarket.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecomarket.category.dto.CategoryRequest;
import com.ecomarket.category.dto.CategoryResponse;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        Category c = repository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return toDto(c);
    }

    public CategoryResponse create(CategoryRequest req) {
        if (repository.findByName(req.getName()).isPresent()) {
            throw new RuntimeException("Category with that name already exists");
        }
        Category c = new Category();
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        Category saved = repository.save(c);
        return toDto(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = repository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        Category saved = repository.save(c);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Category not found");
        repository.deleteById(id);
    }

    private CategoryResponse toDto(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getCreatedAt(), c.getUpdatedAt());
    }
}

package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.domain.model.Category;
import com.ecomarket.domain.repository.CategoryRepository;
import com.ecomarket.infrastructure.persistence.mapper.CategoryEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    
    private final JpaCategoryRepository jpaRepository;
    private final CategoryEntityMapper mapper;
    
    @Override
    public Category save(Category category) {
        var entity = mapper.toEntity(category);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findByActiveTrue() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
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

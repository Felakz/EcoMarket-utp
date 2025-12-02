package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.domain.model.Role;
import com.ecomarket.domain.repository.RoleRepository;
import com.ecomarket.infrastructure.persistence.mapper.RoleEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    
    private final JpaRoleRepository jpaRepository;
    private final RoleEntityMapper mapper;
    
    @Override
    public Role save(Role role) {
        var entity = mapper.toEntity(role);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Role> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}

package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para RoleEntity
 */
@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByName(String name);
    
    boolean existsByName(String name);
}

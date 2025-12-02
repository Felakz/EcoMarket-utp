package com.ecomarket.domain.repository;

import com.ecomarket.domain.model.Role;

import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para Role
 */
public interface RoleRepository {
    
    Role save(Role role);
    
    Optional<Role> findById(Long id);
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
}

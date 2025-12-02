package com.ecomarket.domain.repository;

import com.ecomarket.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para User
 * Define el contrato sin dependencias de infraestructura
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findAll();
    
    List<User> findByActiveTrue();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void deleteById(Long id);
}

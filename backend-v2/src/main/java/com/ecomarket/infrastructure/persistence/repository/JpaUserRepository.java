package com.ecomarket.infrastructure.persistence.repository;

import com.ecomarket.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para UserEntity con Query Methods
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    
    // Query Methods derivados del nombre
    Optional<UserEntity> findByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);
    
    List<UserEntity> findByIsActiveTrue();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Query personalizado con @Query
    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRoles(@Param("username") String username);
    
    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
}

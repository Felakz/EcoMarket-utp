package com.ecomarket.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Role Domain Entity - Representa un rol en el dominio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    private Long id;
    private String name;
    private String description;
    
    // Constantes de roles
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";
    
    public boolean isAdmin() {
        return ADMIN.equals(this.name);
    }
    
    public boolean isUser() {
        return USER.equals(this.name);
    }
}

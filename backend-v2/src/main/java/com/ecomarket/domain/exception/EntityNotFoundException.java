package com.ecomarket.domain.exception;

/**
 * Excepción de dominio cuando no se encuentra una entidad
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

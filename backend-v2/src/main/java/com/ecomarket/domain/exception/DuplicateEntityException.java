package com.ecomarket.domain.exception;

/**
 * Excepción de dominio para duplicados
 */
public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}

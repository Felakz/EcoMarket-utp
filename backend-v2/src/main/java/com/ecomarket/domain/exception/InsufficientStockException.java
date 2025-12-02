package com.ecomarket.domain.exception;

/**
 * Excepción de dominio para stock insuficiente
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

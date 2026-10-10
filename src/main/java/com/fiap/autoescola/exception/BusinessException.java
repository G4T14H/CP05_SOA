package com.fiap.autoescola.exception;

/** Violação de uma regra de negócio (ex.: horário fora do funcionamento). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

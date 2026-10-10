package com.fiap.autoescola.exception;

/** Ex.: tentar alterar um campo imutável, ou e-mail/CPF/CNH duplicado. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

package com.fiap.autoescola.dto.auth;

public record LoginResponse(String token, String tipo) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}

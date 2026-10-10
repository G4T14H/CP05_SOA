package com.fiap.autoescola.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErro(LocalDateTime timestamp, int status, String erro, List<String> mensagens) {
    public ApiErro(int status, String erro, List<String> mensagens) {
        this(LocalDateTime.now(), status, erro, mensagens);
    }
}

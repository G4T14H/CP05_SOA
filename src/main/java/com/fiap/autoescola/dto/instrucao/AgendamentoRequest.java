package com.fiap.autoescola.dto.instrucao;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequest(

        @NotNull(message = "Aluno é obrigatório")
        Long alunoId,

        /** Opcional: se nulo, o sistema escolhe um instrutor disponível aleatoriamente. */
        Long instrutorId,

        @NotNull(message = "Data/hora é obrigatória")
        @Future(message = "Data/hora deve ser no futuro")
        LocalDateTime dataHora
) {}

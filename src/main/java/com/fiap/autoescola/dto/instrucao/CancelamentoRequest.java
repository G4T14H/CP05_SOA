package com.fiap.autoescola.dto.instrucao;

import com.fiap.autoescola.domain.enums.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record CancelamentoRequest(
        @NotNull(message = "Motivo do cancelamento é obrigatório")
        MotivoCancelamento motivo
) {}

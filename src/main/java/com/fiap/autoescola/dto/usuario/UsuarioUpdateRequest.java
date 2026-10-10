package com.fiap.autoescola.dto.usuario;

import com.fiap.autoescola.domain.enums.RoleUsuario;
import jakarta.validation.constraints.NotNull;

/** Atualização do perfil de um usuário, feita apenas por ADMIN. */
public record UsuarioUpdateRequest(
        @NotNull(message = "Role é obrigatória")
        RoleUsuario role,

        @NotNull(message = "Situação (ativo) é obrigatória")
        Boolean ativo
) {}

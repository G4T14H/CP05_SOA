package com.fiap.autoescola.dto.usuario;

import com.fiap.autoescola.domain.enums.RoleUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Usado apenas pelo ADMIN para cadastrar novos usuários do sistema. */
public record UsuarioRequest(

        @NotBlank(message = "Username é obrigatório")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotNull(message = "Role é obrigatória")
        RoleUsuario role
) {}

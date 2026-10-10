package com.fiap.autoescola.dto.usuario;

import com.fiap.autoescola.domain.entity.Usuario;
import com.fiap.autoescola.domain.enums.RoleUsuario;

public record UsuarioResponse(Long id, String username, RoleUsuario role, boolean ativo) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getRole(), u.isAtivo());
    }
}

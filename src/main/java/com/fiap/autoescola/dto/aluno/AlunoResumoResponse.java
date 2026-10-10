package com.fiap.autoescola.dto.aluno;

import com.fiap.autoescola.domain.entity.Aluno;

/** Usado na listagem: nome, e-mail e CPF. */
public record AlunoResumoResponse(Long id, String nome, String email, String cpf) {
    public static AlunoResumoResponse from(Aluno a) {
        return new AlunoResumoResponse(a.getId(), a.getNome(), a.getEmail(), a.getCpf());
    }
}

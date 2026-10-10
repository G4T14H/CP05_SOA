package com.fiap.autoescola.dto.instrutor;

import com.fiap.autoescola.domain.entity.Instrutor;
import com.fiap.autoescola.domain.enums.Especialidade;

/** Usado na listagem: nome, e-mail, CNH e especialidade. */
public record InstrutorResumoResponse(Long id, String nome, String email, String cnh, Especialidade especialidade) {
    public static InstrutorResumoResponse from(Instrutor i) {
        return new InstrutorResumoResponse(i.getId(), i.getNome(), i.getEmail(), i.getCnh(), i.getEspecialidade());
    }
}

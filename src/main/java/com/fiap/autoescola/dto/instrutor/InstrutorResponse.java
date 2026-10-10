package com.fiap.autoescola.dto.instrutor;

import com.fiap.autoescola.domain.entity.Instrutor;
import com.fiap.autoescola.domain.enums.Especialidade;
import com.fiap.autoescola.dto.EnderecoDTO;

public record InstrutorResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cnh,
        Especialidade especialidade,
        EnderecoDTO endereco,
        boolean ativo
) {
    public static InstrutorResponse from(Instrutor i) {
        EnderecoDTO end = new EnderecoDTO(
                i.getEndereco().getLogradouro(), i.getEndereco().getNumero(), i.getEndereco().getComplemento(),
                i.getEndereco().getBairro(), i.getEndereco().getCidade(), i.getEndereco().getUf(), i.getEndereco().getCep());
        return new InstrutorResponse(i.getId(), i.getNome(), i.getEmail(), i.getTelefone(), i.getCnh(),
                i.getEspecialidade(), end, i.isAtivo());
    }
}

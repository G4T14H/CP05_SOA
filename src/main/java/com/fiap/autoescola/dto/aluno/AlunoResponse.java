package com.fiap.autoescola.dto.aluno;

import com.fiap.autoescola.domain.entity.Aluno;
import com.fiap.autoescola.dto.EnderecoDTO;

public record AlunoResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        EnderecoDTO endereco,
        boolean ativo
) {
    public static AlunoResponse from(Aluno a) {
        EnderecoDTO end = new EnderecoDTO(
                a.getEndereco().getLogradouro(), a.getEndereco().getNumero(), a.getEndereco().getComplemento(),
                a.getEndereco().getBairro(), a.getEndereco().getCidade(), a.getEndereco().getUf(), a.getEndereco().getCep());
        return new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getTelefone(), a.getCpf(), end, a.isAtivo());
    }
}

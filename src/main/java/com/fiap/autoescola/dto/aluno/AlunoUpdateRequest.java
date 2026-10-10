package com.fiap.autoescola.dto.aluno;

import com.fiap.autoescola.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Somente nome, telefone e endereço podem ser atualizados.
 * E-mail e CPF são imutáveis após o cadastro.
 */
public record AlunoUpdateRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoDTO endereco
) {}

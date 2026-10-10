package com.fiap.autoescola.dto.aluno;

import com.fiap.autoescola.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunoRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoDTO endereco
) {}

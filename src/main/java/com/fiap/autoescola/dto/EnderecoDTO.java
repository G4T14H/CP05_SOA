package com.fiap.autoescola.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoDTO(

        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,

        String numero,

        String complemento,

        @NotBlank(message = "Bairro é obrigatório")
        String bairro,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotBlank(message = "UF é obrigatória")
        @Size(min = 2, max = 2, message = "UF deve conter exatamente 2 caracteres")
        String uf,

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
        String cep
) {}

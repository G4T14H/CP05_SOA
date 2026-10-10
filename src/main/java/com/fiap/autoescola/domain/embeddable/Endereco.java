package com.fiap.autoescola.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @NotBlank(message = "Logradouro é obrigatório")
    @Column(nullable = false)
    private String logradouro;

    private String numero;

    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Column(nullable = false)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve conter exatamente 2 caracteres")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    @Column(nullable = false)
    private String cep;
}

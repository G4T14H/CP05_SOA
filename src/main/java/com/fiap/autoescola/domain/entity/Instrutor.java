package com.fiap.autoescola.domain.entity;

import com.fiap.autoescola.domain.embeddable.Endereco;
import com.fiap.autoescola.domain.enums.Especialidade;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instrutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Version
    private Long versao;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String cnh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;

    /** Exclusão lógica: instrutor excluído vira inativo, nunca é apagado. */
    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;
}

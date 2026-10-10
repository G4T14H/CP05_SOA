package com.fiap.autoescola.domain.entity;

import com.fiap.autoescola.domain.enums.MotivoCancelamento;
import com.fiap.autoescola.domain.enums.StatusInstrucao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "instrucoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Version
    private Long versao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "instrutor_id", nullable = false)
    private Instrutor instrutor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusInstrucao status = StatusInstrucao.AGENDADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento")
    private MotivoCancelamento motivoCancelamento;
}

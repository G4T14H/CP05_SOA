package com.fiap.autoescola.repository;

import com.fiap.autoescola.domain.entity.Instrucao;
import com.fiap.autoescola.domain.enums.StatusInstrucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Instrucao e where e.id = :id")
    java.util.Optional<Instrucao> bloquear(@org.springframework.data.repository.query.Param("id") Long id);


    long countByAluno_IdAndStatusAndDataHoraBetween(Long alunoId, StatusInstrucao status,
                                                     LocalDateTime inicio, LocalDateTime fim);

    boolean existsByInstrutor_IdAndDataHoraAndStatus(Long instrutorId, LocalDateTime dataHora, StatusInstrucao status);

    Page<Instrucao> findAllByOrderByDataHoraAsc(Pageable pageable);
    @org.springframework.data.jpa.repository.Query("select i from Instrucao i where i.status = com.fiap.autoescola.domain.enums.StatusInstrucao.AGENDADA and (i.aluno.id = :alunoId or i.instrutor.id = :instrutorId)")
    java.util.List<Instrucao> agendas(@org.springframework.data.repository.query.Param("alunoId") Long alunoId, @org.springframework.data.repository.query.Param("instrutorId") Long instrutorId);
}

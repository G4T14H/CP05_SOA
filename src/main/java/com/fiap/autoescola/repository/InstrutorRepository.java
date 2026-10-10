package com.fiap.autoescola.repository;

import com.fiap.autoescola.domain.entity.Instrutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Instrutor e where e.id = :id")
    java.util.Optional<Instrutor> bloquear(@org.springframework.data.repository.query.Param("id") Long id);


    Page<Instrutor> findAllByOrderByNomeAsc(Pageable pageable);

    Optional<Instrutor> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCnh(String cnh);

    @Query("select i from Instrutor i where i.ativo = true and i.id not in " +
           "(select ins.instrutor.id from Instrucao ins where ins.dataHora = :dataHora and ins.status = 'AGENDADA')")
    List<Instrutor> findDisponiveisPorDataHora(@Param("dataHora") LocalDateTime dataHora);
}

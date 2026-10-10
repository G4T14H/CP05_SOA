package com.fiap.autoescola.repository;

import com.fiap.autoescola.domain.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Aluno e where e.id = :id")
    java.util.Optional<Aluno> bloquear(@org.springframework.data.repository.query.Param("id") Long id);


    Page<Aluno> findAllByOrderByNomeAsc(Pageable pageable);

    Optional<Aluno> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}

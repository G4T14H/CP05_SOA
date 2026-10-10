package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.aluno.AlunoRequest;
import com.fiap.autoescola.dto.aluno.AlunoResponse;
import com.fiap.autoescola.dto.aluno.AlunoResumoResponse;
import com.fiap.autoescola.dto.aluno.AlunoUpdateRequest;
import com.fiap.autoescola.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@RequestBody @Valid AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<Page<AlunoResumoResponse>> listar(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(alunoService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid AlunoUpdateRequest request) {
        return ResponseEntity.ok(alunoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.instrutor.InstrutorRequest;
import com.fiap.autoescola.dto.instrutor.InstrutorResponse;
import com.fiap.autoescola.dto.instrutor.InstrutorResumoResponse;
import com.fiap.autoescola.dto.instrutor.InstrutorUpdateRequest;
import com.fiap.autoescola.service.InstrutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrutores")
@RequiredArgsConstructor
public class InstrutorController {

    private final InstrutorService instrutorService;

    @PostMapping
    public ResponseEntity<InstrutorResponse> cadastrar(@RequestBody @Valid InstrutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(instrutorService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<Page<InstrutorResumoResponse>> listar(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(instrutorService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrutorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(instrutorService.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstrutorResponse> atualizar(@PathVariable Long id, @RequestBody @Valid InstrutorUpdateRequest request) {
        return ResponseEntity.ok(instrutorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        instrutorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

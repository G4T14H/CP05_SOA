package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.instrucao.AgendamentoRequest;
import com.fiap.autoescola.dto.instrucao.CancelamentoRequest;
import com.fiap.autoescola.dto.instrucao.InstrucaoResponse;
import com.fiap.autoescola.service.InstrucaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrucoes")
@RequiredArgsConstructor
public class InstrucaoController {

    private final InstrucaoService instrucaoService;

    @PostMapping
    public ResponseEntity<InstrucaoResponse> agendar(@RequestBody @Valid AgendamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(instrucaoService.agendar(request));
    }

    @GetMapping
    public ResponseEntity<Page<InstrucaoResponse>> listar(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(instrucaoService.listar(pageable));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<InstrucaoResponse> cancelar(@PathVariable Long id, @RequestBody @Valid CancelamentoRequest request) {
        return ResponseEntity.ok(instrucaoService.cancelar(id, request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<InstrucaoResponse> buscar(@PathVariable Long id){return ResponseEntity.ok(instrucaoService.buscar(id));}
    @PutMapping("/{id}")
    public ResponseEntity<InstrucaoResponse> reagendar(@PathVariable Long id,@RequestBody @Valid AgendamentoRequest request){return ResponseEntity.ok(instrucaoService.reagendar(id,request));}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id){instrucaoService.excluir(id);return ResponseEntity.noContent().build();}
}

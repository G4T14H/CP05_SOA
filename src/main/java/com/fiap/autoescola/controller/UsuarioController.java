package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.usuario.AlterarSenhaRequest;
import com.fiap.autoescola.dto.usuario.UsuarioRequest;
import com.fiap.autoescola.dto.usuario.UsuarioResponse;
import com.fiap.autoescola.dto.usuario.UsuarioUpdateRequest;
import com.fiap.autoescola.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Cadastro, listagem, atualização e exclusão de usuários: restrito a ADMIN.
 * Alteração da própria senha: liberado para qualquer usuário autenticado.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioResponse>> listar(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarPropriaSenha(Authentication authentication,
                                                      @RequestBody @Valid AlterarSenhaRequest request) {
        usuarioService.alterarPropriaSenha(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id){return ResponseEntity.ok(usuarioService.buscar(id));}
}

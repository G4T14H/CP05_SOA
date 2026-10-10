package com.fiap.autoescola.service;

import com.fiap.autoescola.domain.entity.Usuario;
import com.fiap.autoescola.dto.usuario.AlterarSenhaRequest;
import com.fiap.autoescola.dto.usuario.UsuarioRequest;
import com.fiap.autoescola.dto.usuario.UsuarioResponse;
import com.fiap.autoescola.dto.usuario.UsuarioUpdateRequest;
import com.fiap.autoescola.exception.ConflictException;
import com.fiap.autoescola.exception.ResourceNotFoundException;
import com.fiap.autoescola.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras da CP4:
 * - Apenas ADMIN pode cadastrar, listar, atualizar e excluir usuários (garantido via @PreAuthorize no controller).
 * - A senha nunca é armazenada em texto puro (BCrypt).
 * - Qualquer usuário autenticado pode alterar a própria senha.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ConflictException("Já existe um usuário com este username");
        }

        validarTamanhoSenha(request.senha());
        Usuario usuario = Usuario.builder()
                .username(request.username())
                .senha(passwordEncoder.encode(request.senha()))
                .role(request.role())
                .ativo(true)
                .build();

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAllByOrderByUsernameAsc(pageable).map(UsuarioResponse::from);
    }

    @Transactional(readOnly=true)
    public UsuarioResponse buscar(Long id){return UsuarioResponse.from(buscarPorId(id));}

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorId(id);
        usuario.setRole(request.role());
        usuario.setAtivo(request.ativo());
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
    }

    @Transactional
    public void alterarPropriaSenha(String username, AlterarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new ConflictException("Senha atual incorreta");
        }

        validarTamanhoSenha(request.novaSenha());
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }
    private void validarTamanhoSenha(String senha){
        if(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new com.fiap.autoescola.exception.BusinessException("Senha deve ocupar no máximo 72 bytes UTF-8");
    }
}

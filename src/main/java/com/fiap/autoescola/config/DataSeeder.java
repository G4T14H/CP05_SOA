package com.fiap.autoescola.config;

import com.fiap.autoescola.domain.entity.Usuario;
import com.fiap.autoescola.domain.enums.RoleUsuario;
import com.fiap.autoescola.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garante que sempre exista pelo menos 1 usuário administrador cadastrado,
 * já que o cadastro de usuários exige um admin previamente existente.
 * As credenciais podem (e devem, em produção) ser sobrescritas via variáveis de ambiente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.admin.username}")
    private String adminUsername;

    @Value("${security.admin.senha}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByUsername(adminUsername)) {
            return;
        }

        Usuario admin = Usuario.builder()
                .username(adminUsername)
                .senha(passwordEncoder.encode(adminSenha))
                .role(RoleUsuario.ADMIN)
                .ativo(true)
                .build();

        usuarioRepository.save(admin);
        log.info("Usuário administrador padrão criado: {}", adminUsername);
    }
}

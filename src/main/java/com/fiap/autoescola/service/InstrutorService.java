package com.fiap.autoescola.service;

import com.fiap.autoescola.domain.embeddable.Endereco;
import com.fiap.autoescola.domain.entity.Instrutor;
import com.fiap.autoescola.dto.EnderecoDTO;
import com.fiap.autoescola.dto.instrutor.InstrutorRequest;
import com.fiap.autoescola.dto.instrutor.InstrutorResponse;
import com.fiap.autoescola.dto.instrutor.InstrutorResumoResponse;
import com.fiap.autoescola.dto.instrutor.InstrutorUpdateRequest;
import com.fiap.autoescola.exception.ConflictException;
import com.fiap.autoescola.exception.ResourceNotFoundException;
import com.fiap.autoescola.repository.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrutorService {

    private final InstrutorRepository instrutorRepository;

    @Transactional
    public InstrutorResponse cadastrar(InstrutorRequest request) {
        if (instrutorRepository.existsByEmail(request.email())) {
            throw new ConflictException("Já existe um instrutor com este e-mail");
        }
        if (instrutorRepository.existsByCnh(request.cnh())) {
            throw new ConflictException("Já existe um instrutor com esta CNH");
        }

        Instrutor instrutor = Instrutor.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .cnh(request.cnh())
                .especialidade(request.especialidade())
                .endereco(toEndereco(request.endereco()))
                .ativo(true)
                .build();

        return InstrutorResponse.from(instrutorRepository.save(instrutor));
    }

    @Transactional(readOnly = true)
    public Page<InstrutorResumoResponse> listar(Pageable pageable) {
        return instrutorRepository.findAllByOrderByNomeAsc(pageable).map(InstrutorResumoResponse::from);
    }

    @Transactional(readOnly = true)
    public InstrutorResponse buscar(Long id) {
        return InstrutorResponse.from(buscarEntidade(id));
    }

    @Transactional
    public InstrutorResponse atualizar(Long id, InstrutorUpdateRequest request) {
        Instrutor instrutor = buscarEntidade(id);
        // e-mail, CNH e especialidade são imutáveis por regra de negócio
        instrutor.setNome(request.nome());
        instrutor.setTelefone(request.telefone());
        instrutor.setEndereco(toEndereco(request.endereco()));
        return InstrutorResponse.from(instrutor);
    }

    @Transactional
    public void excluir(Long id) {
        Instrutor instrutor = buscarEntidade(id);
        instrutor.setAtivo(false);
    }

    protected Instrutor buscarEntidade(Long id) {
        return instrutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrutor não encontrado: " + id));
    }

    private Endereco toEndereco(EnderecoDTO dto) {
        return new Endereco(dto.logradouro(), dto.numero(), dto.complemento(), dto.bairro(), dto.cidade(), dto.uf(), dto.cep());
    }
}

package com.fiap.autoescola.service;

import com.fiap.autoescola.domain.embeddable.Endereco;
import com.fiap.autoescola.domain.entity.Aluno;
import com.fiap.autoescola.dto.EnderecoDTO;
import com.fiap.autoescola.dto.aluno.AlunoRequest;
import com.fiap.autoescola.dto.aluno.AlunoResponse;
import com.fiap.autoescola.dto.aluno.AlunoResumoResponse;
import com.fiap.autoescola.dto.aluno.AlunoUpdateRequest;
import com.fiap.autoescola.exception.ConflictException;
import com.fiap.autoescola.exception.ResourceNotFoundException;
import com.fiap.autoescola.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    @Transactional
    public AlunoResponse cadastrar(AlunoRequest request) {
        if (alunoRepository.existsByEmail(request.email())) {
            throw new ConflictException("Já existe um aluno com este e-mail");
        }
        if (alunoRepository.existsByCpf(request.cpf())) {
            throw new ConflictException("Já existe um aluno com este CPF");
        }

        Aluno aluno = Aluno.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .cpf(request.cpf())
                .endereco(toEndereco(request.endereco()))
                .ativo(true)
                .build();

        return AlunoResponse.from(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public Page<AlunoResumoResponse> listar(Pageable pageable) {
        return alunoRepository.findAllByOrderByNomeAsc(pageable).map(AlunoResumoResponse::from);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id) {
        return AlunoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoUpdateRequest request) {
        Aluno aluno = buscarEntidade(id);
        // e-mail e CPF são imutáveis por regra de negócio
        aluno.setNome(request.nome());
        aluno.setTelefone(request.telefone());
        aluno.setEndereco(toEndereco(request.endereco()));
        return AlunoResponse.from(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = buscarEntidade(id);
        aluno.setAtivo(false);
    }

    protected Aluno buscarEntidade(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + id));
    }

    private Endereco toEndereco(EnderecoDTO dto) {
        return new Endereco(dto.logradouro(), dto.numero(), dto.complemento(), dto.bairro(), dto.cidade(), dto.uf(), dto.cep());
    }
}

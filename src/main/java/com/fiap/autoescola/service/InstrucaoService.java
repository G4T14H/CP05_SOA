package com.fiap.autoescola.service;

import com.fiap.autoescola.domain.entity.*;
import com.fiap.autoescola.domain.enums.*;
import com.fiap.autoescola.dto.instrucao.*;
import com.fiap.autoescola.exception.*;
import com.fiap.autoescola.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
@Service
@RequiredArgsConstructor
@Transactional
public class InstrucaoService {
    private final InstrucaoRepository instrucaoRepository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final Clock clock;

    public InstrucaoResponse agendar(AgendamentoRequest request){
        var instrucao=Instrucao.builder().status(StatusInstrucao.AGENDADA).build();
        preencher(instrucao,request);return InstrucaoResponse.from(instrucaoRepository.saveAndFlush(instrucao));
    }
    @Transactional(readOnly=true)
    public Page<InstrucaoResponse> listar(Pageable pageable){return instrucaoRepository.findAllByOrderByDataHoraAsc(pageable).map(InstrucaoResponse::from);}
    @Transactional(readOnly=true)
    public InstrucaoResponse buscar(Long id){return InstrucaoResponse.from(instrucaoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Instrução não encontrada: "+id)));}
    public InstrucaoResponse reagendar(Long id,AgendamentoRequest request){
        var instrucao=bloquear(id);exigirCancelamentoPermitido(instrucao);preencher(instrucao,request);
        return InstrucaoResponse.from(instrucaoRepository.saveAndFlush(instrucao));
    }
    public InstrucaoResponse cancelar(Long id,CancelamentoRequest request){
        var instrucao=bloquear(id);exigirCancelamentoPermitido(instrucao);
        instrucao.setStatus(StatusInstrucao.CANCELADA);instrucao.setMotivoCancelamento(request.motivo());
        return InstrucaoResponse.from(instrucaoRepository.saveAndFlush(instrucao));
    }
    /** DELETE realiza cancelamento lógico com OUTROS, preservando o histórico. */
    public void excluir(Long id){
        var instrucao=bloquear(id);
        if(instrucao.getStatus()==StatusInstrucao.CANCELADA)return;
        exigirCancelamentoPermitido(instrucao);instrucao.setStatus(StatusInstrucao.CANCELADA);instrucao.setMotivoCancelamento(MotivoCancelamento.OUTROS);
    }
    private Instrucao bloquear(Long id){return instrucaoRepository.bloquear(id).orElseThrow(()->new ResourceNotFoundException("Instrução não encontrada: "+id));}
    private void exigirCancelamentoPermitido(Instrucao instrucao){
        if(instrucao.getStatus()!=StatusInstrucao.AGENDADA)throw new BusinessException("Instrução já está cancelada");
        if(instrucao.getDataHora().isBefore(LocalDateTime.now(clock).plusHours(24)))
            throw new BusinessException("Cancelamento ou reagendamento exige antecedência mínima de 24 horas");
    }
    private void preencher(Instrucao instrucao,AgendamentoRequest request){
        validarHorario(request.dataHora());
        var aluno=alunoRepository.bloquear(request.alunoId()).orElseThrow(()->new ResourceNotFoundException("Aluno não encontrado: "+request.alunoId()));
        if(!aluno.isAtivo())throw new BusinessException("Não é possível agendar instrução para aluno inativo");
        var inicioDia=request.dataHora().toLocalDate().atStartOfDay();
        long total=instrucaoRepository.countByAluno_IdAndStatusAndDataHoraBetween(aluno.getId(),StatusInstrucao.AGENDADA,inicioDia,inicioDia.plusDays(1).minusNanos(1));
        if(instrucao.getId()!=null && instrucao.getAluno().getId().equals(aluno.getId()) && instrucao.getDataHora().toLocalDate().equals(request.dataHora().toLocalDate()))total--;
        if(total>=2)throw new BusinessException("Aluno já possui o máximo de 2 instruções agendadas neste dia");
        Instrutor instrutor;
        if(request.instrutorId()!=null){
            instrutor=instrutorRepository.bloquear(request.instrutorId()).orElseThrow(()->new ResourceNotFoundException("Instrutor não encontrado: "+request.instrutorId()));
            if(!instrutor.isAtivo())throw new BusinessException("Não é possível agendar instrução com instrutor inativo");
            if(!disponivel(aluno.getId(),instrutor.getId(),request.dataHora(),instrucao.getId()))throw new BusinessException("Aluno ou instrutor possui instrução sobreposta neste horário");
        }else{
            // Ordem crescente de locks evita ciclos. Sorteio ocorre apenas entre ativos e livres.
            var disponiveis=new ArrayList<Instrutor>();
            for(var candidato:instrutorRepository.findAll(Sort.by("id"))){
                var bloqueado=instrutorRepository.bloquear(candidato.getId()).orElseThrow();
                if(bloqueado.isAtivo() && disponivel(aluno.getId(),bloqueado.getId(),request.dataHora(),instrucao.getId()))disponiveis.add(bloqueado);
            }
            if(disponiveis.isEmpty())throw new BusinessException("Não há instrutor disponível nesta data/hora");
            instrutor=disponiveis.get(ThreadLocalRandom.current().nextInt(disponiveis.size()));
        }
        instrucao.setAluno(aluno);instrucao.setInstrutor(instrutor);instrucao.setDataHora(request.dataHora());
    }
    private boolean disponivel(Long alunoId,Long instrutorId,LocalDateTime inicio,Long ignorar){
        for(var outra:instrucaoRepository.agendas(alunoId,instrutorId)){
            if(!Objects.equals(outra.getId(),ignorar) && inicio.isBefore(outra.getDataHora().plusHours(1)) && inicio.plusHours(1).isAfter(outra.getDataHora()))return false;
        }
        return true;
    }
    private void validarHorario(LocalDateTime dataHora){
        if(dataHora.getDayOfWeek()==DayOfWeek.SUNDAY)throw new BusinessException("A auto-escola não funciona aos domingos");
        var hora=dataHora.toLocalTime();
        if(hora.isBefore(LocalTime.of(6,0)) || hora.isAfter(LocalTime.of(20,0)))throw new BusinessException("Horário fora do funcionamento (06:00 às 21:00, instruções fixas de 1h)");
        if(dataHora.isBefore(LocalDateTime.now(clock).plusMinutes(30)))throw new BusinessException("Agendamento exige antecedência mínima de 30 minutos");
    }
}

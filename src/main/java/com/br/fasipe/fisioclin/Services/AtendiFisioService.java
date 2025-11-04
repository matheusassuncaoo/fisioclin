package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.AtendiFisio;
import com.br.fasipe.fisioclin.Repositories.AtendiFisioRepository;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AtendiFisioService {
    
    @Autowired
    private AtendiFisioRepository atendiFisioRepository;
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Transactional(readOnly = true)
    public List<AtendiFisio> listarTodos() {
        return atendiFisioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<AtendiFisio> buscarPorId(Integer id) {
        return atendiFisioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<AtendiFisio> buscarPorPaciente(Integer idPaciente) {
        return atendiFisioRepository.findByIdPacienteOrderByDataAtendiDesc(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public List<AtendiFisio> buscarPorProfissional(Integer idProfissio) {
        return atendiFisioRepository.findByIdProfissioOrderByDataAtendiDesc(idProfissio);
    }
    
    @Transactional(readOnly = true)
    public List<AtendiFisio> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return atendiFisioRepository.findByDataAtendiBetweenOrderByDataAtendiDesc(dataInicio, dataFim);
    }
    
    @Transactional(readOnly = true)
    public List<AtendiFisio> buscarEvolucaoPaciente(Integer idPaciente, LocalDate dataInicio, LocalDate dataFim) {
        // Validar se paciente existe e está ativo
        if (!pacienteRepository.isPacienteAtivo(idPaciente)) {
            throw new IllegalArgumentException("Paciente não encontrado ou inativo");
        }
        
        return atendiFisioRepository.findByPacienteAndPeriodo(idPaciente, dataInicio, dataFim);
    }
    
    @Transactional(readOnly = true)
    public AtendiFisio buscarUltimoAtendimento(Integer idPaciente) {
        return atendiFisioRepository.findUltimoAtendimento(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public Long contarAtendimentos(Integer idPaciente) {
        return atendiFisioRepository.countByIdPaciente(idPaciente);
    }
    
    @Transactional
    public AtendiFisio salvar(AtendiFisio atendimento) {
        // Validações
        if (atendimento.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
        
        if (atendimento.getIdProfissio() == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
        
        if (atendimento.getDataAtendi() == null) {
            throw new IllegalArgumentException("Data do atendimento é obrigatória");
        }
        
        // Verificar se paciente está ativo
        if (!pacienteRepository.isPacienteAtivo(atendimento.getIdPaciente())) {
            throw new IllegalStateException("Paciente inativo não pode ter atendimentos");
        }
        
        return atendiFisioRepository.save(atendimento);
    }
    
    @Transactional
    public AtendiFisio atualizar(Integer id, AtendiFisio atendimento) {
        Optional<AtendiFisio> existente = atendiFisioRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Atendimento não encontrado");
        }
        
        atendimento.setIdAtendiFisio(id);
        return atendiFisioRepository.save(atendimento);
    }
    
    @Transactional
    public void deletar(Integer id) {
        if (!atendiFisioRepository.existsById(id)) {
            throw new IllegalArgumentException("Atendimento não encontrado");
        }
        atendiFisioRepository.deleteById(id);
    }
}

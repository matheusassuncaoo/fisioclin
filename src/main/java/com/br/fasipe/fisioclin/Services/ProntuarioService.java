package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.Prontuario;
import com.br.fasipe.fisioclin.Repositories.ProntuarioRepository;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProntuarioService {
    
    @Autowired
    private ProntuarioRepository prontuarioRepository;
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Transactional(readOnly = true)
    public List<Prontuario> listarTodos() {
        return prontuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Prontuario> buscarPorId(Integer id) {
        return prontuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Prontuario> buscarEvolucaoPaciente(Integer idPaciente) {
        return prontuarioRepository.findByIdPacienteOrderByDataProcedDesc(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public List<Prontuario> buscarEvolucaoPorPeriodo(Integer idPaciente, LocalDate dataInicio, LocalDate dataFim) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }
        
        return prontuarioRepository.findByPacienteAndPeriodo(idPaciente, dataInicio, dataFim);
    }
    
    @Transactional(readOnly = true)
    public List<Prontuario> buscarPorProfissional(Integer idProfissio) {
        return prontuarioRepository.findByIdProfissioOrderByDataProcedDesc(idProfissio);
    }
    
    @Transactional(readOnly = true)
    public List<Prontuario> buscarPorEspecialidade(Integer idEspec) {
        return prontuarioRepository.findByIdEspecOrderByDataProcedDesc(idEspec);
    }
    
    @Transactional(readOnly = true)
    public Prontuario buscarUltimoProntuario(Integer idPaciente) {
        return prontuarioRepository.findUltimoProntuario(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public List<Prontuario> buscarProntuariosVisiveis(Integer idPaciente) {
        return prontuarioRepository.findProntuariosVisiveisPaciente(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public Long contarProntuarios(Integer idPaciente) {
        return prontuarioRepository.countByIdPaciente(idPaciente);
    }
    
    @Transactional
    public Prontuario salvar(Prontuario prontuario) {
        // Validações
        if (prontuario.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
        
        if (prontuario.getIdProfissio() == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
        
        if (prontuario.getDescrProntu() == null || prontuario.getDescrProntu().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do prontuário é obrigatória");
        }
        
        // Verificar se paciente está ativo
        if (!pacienteRepository.isPacienteAtivo(prontuario.getIdPaciente())) {
            throw new IllegalStateException("Não é possível criar prontuário para paciente inativo");
        }
        
        return prontuarioRepository.save(prontuario);
    }
    
    @Transactional
    public Prontuario atualizar(Integer id, Prontuario prontuario) {
        Optional<Prontuario> existente = prontuarioRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Prontuário não encontrado");
        }
        
        prontuario.setIdProntu(id);
        return prontuarioRepository.save(prontuario);
    }
    
    @Transactional
    public void deletar(Integer id) {
        if (!prontuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Prontuário não encontrado");
        }
        prontuarioRepository.deleteById(id);
    }
}

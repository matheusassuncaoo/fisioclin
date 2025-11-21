package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.AtendiFisio;
import com.br.fasipe.fisioclin.Models.Procedimento;
import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.Repositories.AtendiFisioRepository;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import com.br.fasipe.fisioclin.Repositories.ProcedimentoRepository;
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
    
    @Autowired
    private ProcedimentoRepository procedimentoRepository;
    
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
    public AtendiFisio criarComSOAP(AtendimentoSOAPDTO dto) {
        // Validações
        if (dto.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
        
        if (dto.getIdProfissio() == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
        
        if (dto.getCodProced() == null || dto.getCodProced().trim().isEmpty()) {
            throw new IllegalArgumentException("Código do procedimento é obrigatório");
        }
        
        if (dto.getDataAtendimento() == null) {
            throw new IllegalArgumentException("Data do atendimento é obrigatória");
        }
        
        // Verificar se paciente está ativo
        if (!pacienteRepository.isPacienteAtivo(dto.getIdPaciente())) {
            throw new IllegalStateException("Paciente inativo não pode ter atendimentos");
        }
        
        // Buscar procedimento por código
        Procedimento procedimento = procedimentoRepository.findByCodProced(dto.getCodProced())
            .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado com código: " + dto.getCodProced()));
        
        // Criar atendimento com SOAP
        AtendiFisio atendimento = new AtendiFisio();
        atendimento.setIdPaciente(dto.getIdPaciente());
        atendimento.setIdProfissio(dto.getIdProfissio());
        atendimento.setIdProced(procedimento.getIdProced());
        atendimento.setDataAtendi(dto.getDataAtendimento());
        
        // Concatenar SOAP em descrAtendi
        StringBuilder descricao = new StringBuilder();
        if (dto.getSubjetivo() != null && !dto.getSubjetivo().trim().isEmpty()) {
            descricao.append("S: ").append(dto.getSubjetivo()).append("\n");
        }
        if (dto.getObjetivo() != null && !dto.getObjetivo().trim().isEmpty()) {
            descricao.append("O: ").append(dto.getObjetivo()).append("\n");
        }
        if (dto.getAvaliacao() != null && !dto.getAvaliacao().trim().isEmpty()) {
            descricao.append("A: ").append(dto.getAvaliacao()).append("\n");
        }
        if (dto.getPlano() != null && !dto.getPlano().trim().isEmpty()) {
            descricao.append("P: ").append(dto.getPlano());
        }
        
        atendimento.setDescrAtendi(descricao.toString());
        
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

package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.Paciente;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Paciente> listarAtivos() {
        return pacienteRepository.findByStatusPacTrue();
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Integer id) {
        return pacienteRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorRg(String rg) {
        return pacienteRepository.findByRgPaciente(rg);
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorIdPessoaFis(Integer idPessoaFis) {
        return pacienteRepository.findByIdPessoaFis(idPessoaFis);
    }
    
    @Transactional
    public Paciente salvar(Paciente paciente) {
        // Validações
        if (paciente.getRgPaciente() == null || paciente.getRgPaciente().trim().isEmpty()) {
            throw new IllegalArgumentException("RG do paciente é obrigatório");
        }
        
        if (paciente.getIdPessoaFis() == null) {
            throw new IllegalArgumentException("ID da pessoa física é obrigatório");
        }
        
        // Verificar se RG já existe
        if (paciente.getIdPaciente() == null) {
            Optional<Paciente> existente = pacienteRepository.findByRgPaciente(paciente.getRgPaciente());
            if (existente.isPresent()) {
                throw new IllegalStateException("RG já cadastrado");
            }
        }
        
        return pacienteRepository.save(paciente);
    }
    
    @Transactional
    public Paciente atualizar(Integer id, Paciente paciente) {
        Optional<Paciente> existente = pacienteRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }
        
        paciente.setIdPaciente(id);
        return pacienteRepository.save(paciente);
    }
    
    @Transactional
    public void inativar(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        
        paciente.setStatusPac(false);
        pacienteRepository.save(paciente);
    }
    
    @Transactional
    public void ativar(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        
        paciente.setStatusPac(true);
        pacienteRepository.save(paciente);
    }
    
    @Transactional(readOnly = true)
    public Long contarAtivos() {
        return pacienteRepository.countPacientesAtivos();
    }
    
    @Transactional(readOnly = true)
    public Boolean isPacienteAtivo(Integer id) {
        return pacienteRepository.isPacienteAtivo(id);
    }
}

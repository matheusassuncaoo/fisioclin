package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.DTOs.PacienteComNomeDTO;
import com.br.fasipe.fisioclin.Models.Paciente;
import com.br.fasipe.fisioclin.Models.PessoaFis;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import com.br.fasipe.fisioclin.Repositories.PessoaFisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service seguindo padrão MVC
 * A lógica de negócio fica aqui, não no Repository
 */
@Service
public class PacienteService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private PessoaFisRepository pessoaFisRepository;
    
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Paciente> listarAtivos() {
        return pacienteRepository.findByStatusPac(1);
    }
    
    @Transactional(readOnly = true)
    public List<Paciente> listarInativos() {
        return pacienteRepository.findByStatusPac(0);
    }
    
    /**
     * Lista pacientes ativos com nome - LÓGICA NO SERVICE (padrão MVC)
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarAtivosComNome() {
        List<Paciente> pacientes = pacienteRepository.findByStatusPac(1);
        return pacientes.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Lista pacientes inativos com nome
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarInativosComNome() {
        List<Paciente> pacientes = pacienteRepository.findByStatusPac(0);
        return pacientes.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Lista todos pacientes com nome
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarTodosComNome() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        return pacientes.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Integer id) {
        return pacienteRepository.findById(id);
    }
    
    /**
     * Busca paciente por ID com nome
     */
    @Transactional(readOnly = true)
    public Optional<PacienteComNomeDTO> buscarPorIdComNome(Integer id) {
        return pacienteRepository.findById(id)
            .map(this::converterParaDTO);
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorRg(String rg) {
        return pacienteRepository.findByRgPaciente(rg);
    }
    
    @Transactional
    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }
    
    @Transactional
    public Paciente atualizar(Integer id, Paciente pacienteAtualizado) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        
        paciente.setRgPaciente(pacienteAtualizado.getRgPaciente());
        paciente.setEstdOrgPac(pacienteAtualizado.getEstdOrgPac());
        paciente.setStatusPac(pacienteAtualizado.getStatusPac());
        paciente.setIdDocumento(pacienteAtualizado.getIdDocumento());
        
        return pacienteRepository.save(paciente);
    }
    
    @Transactional
    public void deletar(Integer id) {
        pacienteRepository.deleteById(id);
    }
    
    @Transactional
    public void inativar(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        paciente.setStatusPac(0); // 0 = inativo
        pacienteRepository.save(paciente);
    }
    
    @Transactional
    public void ativar(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        paciente.setStatusPac(1); // 1 = ativo
        pacienteRepository.save(paciente);
    }
    
    /**
     * Método privado que faz a conversão de Paciente para DTO com nome
     * GAMBIARRA: Preenche valores padrão quando dados estão faltando no banco
     */
    private PacienteComNomeDTO converterParaDTO(Paciente paciente) {
        PacienteComNomeDTO dto = new PacienteComNomeDTO();

        // Dados básicos do paciente
        dto.setIdPaciente(paciente.getIdPaciente());
        dto.setRgPaciente(paciente.getRgPaciente() != null ? paciente.getRgPaciente() : "SEM RG");
        dto.setEstdoRgPac(paciente.getEstdOrgPac() != null ? paciente.getEstdOrgPac() : "SP");
        dto.setStatusPac(paciente.getStatusPac() != null && paciente.getStatusPac() == 1);

        // CPF vindo do IDDOCUMENTO (BigInteger)
        dto.setCpfPessoa(
            paciente.getIdDocumento() != null
                ? paciente.getIdDocumento().toString()
                : "00000000000"
        );

        // GAMBIARRA: Tenta buscar dados de PessoaFis, mas não quebra se não achar
        try {
            Optional<PessoaFis> pessoaFisOpt = pessoaFisRepository.findByIdDocumento(paciente.getIdDocumento());
            if (pessoaFisOpt.isPresent()) {
                PessoaFis pf = pessoaFisOpt.get();
                dto.setSexoPessoa(pf.getSexoPessoa() != null ? pf.getSexoPessoa().name() : "M");
                
                // Tenta pegar nome da tabela Pessoa se existir relacionamento
                if (pf.getPessoa() != null && pf.getPessoa().getNomePessoa() != null) {
                    dto.setNomePessoa(pf.getPessoa().getNomePessoa());
                } else {
                    dto.setNomePessoa("Paciente #" + paciente.getIdPaciente());
                }
            } else {
                // Valores padrão quando não encontra PessoaFis
                dto.setSexoPessoa("M");
                dto.setNomePessoa("Paciente #" + paciente.getIdPaciente());
            }
        } catch (Exception e) {
            // Se der erro, usa valores padrão
            dto.setSexoPessoa("M");
            dto.setNomePessoa("Paciente #" + paciente.getIdPaciente());
        }

        return dto;
    }
    
    @Transactional(readOnly = true)
    public Long contarAtivos() {
        return pacienteRepository.countPacientesAtivos();
    }
    
    @Transactional(readOnly = true)
    public Boolean verificarSeAtivo(Integer id) {
        return pacienteRepository.isPacienteAtivo(id);
    }
}

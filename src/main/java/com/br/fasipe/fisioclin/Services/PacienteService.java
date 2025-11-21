package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.DTOs.PacienteComNomeDTO;
import com.br.fasipe.fisioclin.Models.Paciente;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Paciente> listarInativos() {
        return pacienteRepository.findByStatusPacFalse();
    }
    
    /**
     * Lista todos os pacientes ativos com o nome da pessoa física
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarAtivosComNome() {
        List<Map<String, Object>> resultado = pacienteRepository.findAllPacientesComNomeAtivos();
        return mapToPacienteComNomeDTO(resultado);
    }
    
    /**
     * Lista todos os pacientes inativos com o nome da pessoa física
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarInativosComNome() {
        List<Map<String, Object>> resultado = pacienteRepository.findAllPacientesComNomeInativos();
        return mapToPacienteComNomeDTO(resultado);
    }
    
    /**
     * Lista todos os pacientes com o nome da pessoa física
     */
    @Transactional(readOnly = true)
    public List<PacienteComNomeDTO> listarTodosComNome() {
        List<Map<String, Object>> resultado = pacienteRepository.findAllPacientesComNome();
        return mapToPacienteComNomeDTO(resultado);
    }
    
    /**
     * Busca um paciente por ID com o nome da pessoa física
     */
    @Transactional(readOnly = true)
    public Optional<PacienteComNomeDTO> buscarPorIdComNome(Integer id) {
        Optional<Map<String, Object>> resultado = pacienteRepository.findPacienteComNomeById(id);
        return resultado.map(this::mapSingleToPacienteComNomeDTO);
    }
    
    /**
     * Converte Map do resultado SQL para DTO
     */
    private List<PacienteComNomeDTO> mapToPacienteComNomeDTO(List<Map<String, Object>> resultado) {
        return resultado.stream()
            .map(this::mapSingleToPacienteComNomeDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Converte um único Map do resultado SQL para DTO
     */
    private PacienteComNomeDTO mapSingleToPacienteComNomeDTO(Map<String, Object> row) {
        PacienteComNomeDTO dto = new PacienteComNomeDTO();
        
        dto.setIdPaciente(getIntegerValue(row.get("idPaciente")));
        dto.setIdPessoaFis(getIntegerValue(row.get("idPessoaFis")));
        dto.setRgPaciente((String) row.get("rgPaciente"));
        dto.setEstdoRgPac((String) row.get("estdoRgPac"));
        dto.setStatusPac(getBooleanValue(row.get("statusPac")));
        dto.setNomePessoa((String) row.get("nomePessoa"));
        dto.setCpfPessoa((String) row.get("cpfPessoa"));
        dto.setDataNascPes(getLocalDateValue(row.get("dataNascPes")));
        dto.setSexoPessoa((String) row.get("sexoPessoa"));
        
        return dto;
    }
    
    /**
     * Converte valor do banco para Integer
     */
    private Integer getIntegerValue(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
        return Integer.parseInt(value.toString());
    }
    
    /**
     * Converte valor do banco para Boolean
     */
    private Boolean getBooleanValue(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return Boolean.parseBoolean(value.toString());
    }
    
    /**
     * Converte valor do banco para LocalDate
     */
    private LocalDate getLocalDateValue(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof Date) return ((Date) value).toLocalDate();
        if (value instanceof java.util.Date) {
            return new Date(((java.util.Date) value).getTime()).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Integer id) {
        return pacienteRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorRg(String rg) {
        return pacienteRepository.findByRgPaciente(rg);
    }
    
    @Transactional
    public Paciente salvar(Paciente paciente) {
        // Validações
        if (paciente.getRgPaciente() == null || paciente.getRgPaciente().trim().isEmpty()) {
            throw new IllegalArgumentException("RG do paciente é obrigatório");
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

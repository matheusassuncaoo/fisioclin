package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.DTOs.DashboardDTO;
import com.br.fasipe.fisioclin.DTOs.DashboardDTO.*;
import com.br.fasipe.fisioclin.Models.Anamnese;
import com.br.fasipe.fisioclin.Models.AtendiFisio;
import com.br.fasipe.fisioclin.Repositories.AnamneseRepository;
import com.br.fasipe.fisioclin.Repositories.AtendiFisioRepository;
import com.br.fasipe.fisioclin.Repositories.PacienteRepository;
import com.br.fasipe.fisioclin.Repositories.PessoaFisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para obter dados do Dashboard
 * Fornece estatísticas consolidadas do sistema
 */
@Service
public class DashboardService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private AtendiFisioRepository atendiFisioRepository;
    
    @Autowired
    private AnamneseRepository anamneseRepository;
    
    @Autowired
    private PessoaFisRepository pessoaFisRepository;
    
    /**
     * Obtém dados completos do dashboard
     */
    @Transactional(readOnly = true)
    public DashboardDTO obterDadosDashboard() {
        DashboardDTO dashboard = new DashboardDTO();
        LocalDate hoje = LocalDate.now();
        
        dashboard.setDataReferencia(hoje);
        
        // Total de pacientes ativos (statusPac = 1)
        dashboard.setTotalPacientesAtivos(pacienteRepository.countByStatusPac(1));
        
        // Atendimentos de hoje
        Long atendimentosHoje = atendiFisioRepository.countByData(hoje);
        dashboard.setAtendimentosHoje(atendimentosHoje != null ? atendimentosHoje : 0L);
        
        // Anamneses pendentes
        Long anamnesesPendentes = anamneseRepository.countAnamnesesPendentes();
        dashboard.setAnamnesesPendentes(anamnesesPendentes != null ? anamnesesPendentes : 0L);
        
        return dashboard;
    }
    
    /**
     * Obtém dados do dashboard com detalhes
     */
    @Transactional(readOnly = true)
    public DashboardDTO obterDadosDashboardDetalhado() {
        DashboardDTO dashboard = obterDadosDashboard();
        LocalDate hoje = LocalDate.now();
        
        // Lista de atendimentos de hoje
        List<AtendiFisio> atendimentosHoje = atendiFisioRepository.findByData(hoje);
        List<AtendimentoHojeDTO> atendimentosDTO = atendimentosHoje.stream()
            .map(a -> {
                AtendimentoHojeDTO dto = new AtendimentoHojeDTO();
                dto.setIdAtendiFisio(a.getIdAtendiFisio());
                dto.setIdPaciente(a.getIdPaciente());
                // Buscar nome do paciente (com tratamento de erro)
                try {
                    pessoaFisRepository.findNomePaciente(a.getIdPaciente())
                        .ifPresent(dto::setNomePaciente);
                } catch (Exception e) {
                    // Se der erro, deixa sem nome
                    dto.setNomePaciente("Paciente #" + a.getIdPaciente());
                }
                return dto;
            })
            .collect(Collectors.toList());
        dashboard.setAtendimentosDeHoje(atendimentosDTO);
        
        // Lista de anamneses pendentes
        List<Anamnese> anamnesesPendentes = anamneseRepository.findAnamnesesPendentes();
        List<AnamnesePendenteDTO> anamnesesDTO = anamnesesPendentes.stream()
            .limit(10) // Limitar a 10 para não sobrecarregar
            .map(a -> {
                AnamnesePendenteDTO dto = new AnamnesePendenteDTO();
                dto.setIdAnamnese(a.getIdAnamnese());
                dto.setIdPaciente(a.getIdPaciente());
                dto.setDataAnamnese(a.getDataAnam().toLocalDate());
                dto.setStatus(a.getStatusAnm() != null ? a.getStatusAnm().toString() : "PENDENTE");
                // Buscar nome do paciente (com tratamento de erro)
                try {
                    pessoaFisRepository.findNomePaciente(a.getIdPaciente())
                        .ifPresent(dto::setNomePaciente);
                } catch (Exception e) {
                    // Se der erro, deixa sem nome
                    dto.setNomePaciente("Paciente #" + a.getIdPaciente());
                }
                return dto;
            })
            .collect(Collectors.toList());
        dashboard.setAnamnesesAguardando(anamnesesDTO);
        
        return dashboard;
    }
    
    /**
     * Conta atendimentos de hoje (para uso no dashboard)
     */
    @Transactional(readOnly = true)
    public Long contarAtendimentosHoje() {
        Long count = atendiFisioRepository.countByData(LocalDate.now());
        return count != null ? count : 0L;
    }
    
    /**
     * Conta anamneses pendentes (para uso no dashboard)
     */
    @Transactional(readOnly = true)
    public Long contarAnamnesesPendentes() {
        Long count = anamneseRepository.countAnamnesesPendentes();
        return count != null ? count : 0L;
    }
    
    /**
     * Conta anamneses pendentes de um paciente específico
     */
    @Transactional(readOnly = true)
    public Long contarAnamnesesPendentesPorPaciente(Integer idPaciente) {
        Long count = anamneseRepository.countAnamnesesPendentesByPaciente(idPaciente);
        return count != null ? count : 0L;
    }
}


package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteComNomeDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteDetalhesDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteDetalhesDTO.*;
import com.br.fasipe.fisioclin.Models.*;
import com.br.fasipe.fisioclin.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para consolidar todas as informações do paciente
 * de forma otimizada para visualização rápida no atendimento
 */
@Service
public class PacienteDetalhesService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private AtendiFisioRepository atendiFisioRepository;
    
    @Autowired
    private ProntuarioRepository prontuarioRepository;
    
    @Autowired
    private ExercRealizadoRepository exercRealizadoRepository;
    
    @Autowired
    private AnamneseRepository anamneseRepository;
    
    /**
     * Busca todas as informações consolidadas do paciente
     * de forma otimizada para exibição rápida
     */
    @Transactional(readOnly = true)
    public PacienteDetalhesDTO buscarDetalhesCompletos(Integer idPaciente) {
        // Buscar paciente com dados da pessoa física
        PacienteComNomeDTO pacienteComNome = pacienteService.buscarPorIdComNome(idPaciente)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        
        PacienteDetalhesDTO detalhes = new PacienteDetalhesDTO();
        
        // Dados básicos do paciente
        detalhes.setIdPaciente(pacienteComNome.getIdPaciente());
        detalhes.setRgPaciente(pacienteComNome.getRgPaciente());
        detalhes.setEstdoRgPac(pacienteComNome.getEstdoRgPac());
        detalhes.setStatusPac(pacienteComNome.getStatusPac());
        
        // Dados da pessoa física
        detalhes.setNomePessoa(pacienteComNome.getNomePessoa());
        detalhes.setCpfPessoa(pacienteComNome.getCpfPessoa());
        detalhes.setDataNascPes(pacienteComNome.getDataNascPes());
        detalhes.setSexoPessoa(pacienteComNome.getSexoPessoa());
        
        // Estatísticas
        detalhes.setEstatisticas(calcularEstatisticas(idPaciente));
        
        // Última anamnese
        detalhes.setUltimaAnamnese(buscarUltimaAnamnese(idPaciente));
        
        // Últimos atendimentos
        detalhes.setUltimosAtendimentos(buscarUltimosAtendimentos(idPaciente, 10));
        
        // Exercícios ativos (últimos 30 dias)
        detalhes.setExerciciosAtivos(buscarExerciciosAtivos(idPaciente));
        
        // Evolução recente (últimos 30 dias)
        detalhes.setEvolucaoRecente(calcularEvolucaoRecente(idPaciente, 30));
        
        return detalhes;
    }
    
    /**
     * Calcula estatísticas gerais do paciente
     */
    private EstatisticasDTO calcularEstatisticas(Integer idPaciente) {
        EstatisticasDTO stats = new EstatisticasDTO();
        
        // Total de atendimentos
        stats.setTotalAtendimentos(atendiFisioRepository.countByIdPaciente(idPaciente));
        
        // Total de prontuários
        stats.setTotalProntuarios(prontuarioRepository.countByIdPaciente(idPaciente));
        
        // Total de exercícios realizados
        List<ExercRealizado> exercicios = exercRealizadoRepository.findByPaciente(idPaciente);
        stats.setTotalExerciciosRealizados((long) exercicios.size());
        
        // Primeiro e último atendimento
        AtendiFisio primeiroAtend = atendiFisioRepository.findPrimeiroAtendimento(idPaciente);
        AtendiFisio ultimoAtend = atendiFisioRepository.findUltimoAtendimento(idPaciente);
        
        if (primeiroAtend != null) {
            stats.setPrimeiroAtendimento(primeiroAtend.getDataAtendi());
            
            // Calcular dias em tratamento
            LocalDate hoje = LocalDate.now();
            long dias = ChronoUnit.DAYS.between(primeiroAtend.getDataAtendi(), hoje);
            stats.setDiasEmTratamento((int) dias);
            
            // Calcular frequência semanal
            if (dias > 0 && stats.getTotalAtendimentos() > 0) {
                double semanas = dias / 7.0;
                stats.setFrequenciaSemanal(stats.getTotalAtendimentos() / semanas);
            } else {
                stats.setFrequenciaSemanal(0.0);
            }
        }
        
        if (ultimoAtend != null) {
            stats.setUltimoAtendimento(ultimoAtend.getDataAtendi());
        }
        
        return stats;
    }
    
    /**
     * Busca a última anamnese do paciente
     */
    private AnamneseResumoDTO buscarUltimaAnamnese(Integer idPaciente) {
        List<Anamnese> anamneses = anamneseRepository.findByIdPacienteOrderByDataAnamDesc(idPaciente);
        
        if (anamneses.isEmpty()) {
            return null;
        }
        
        Anamnese ultima = anamneses.get(0);
        AnamneseResumoDTO resumo = new AnamneseResumoDTO();
        resumo.setIdAnamnese(ultima.getIdAnamnese());
        resumo.setDataAnamnese(ultima.getDataAnam().toLocalDate());
        resumo.setStatusAnamnese(ultima.getStatusAnm() != null ? ultima.getStatusAnm().toString() : "");
        // Campos detalhados da anamnese viriam do relacionamento com Resposta/Pergunta
        resumo.setQueixaPrincipal("Informações na anamnese completa");
        resumo.setHistoriaDoenca("Ver anamnese completa");
        resumo.setObservacao(ultima.getObservacoes());
        
        return resumo;
    }
    
    /**
     * Busca os últimos N atendimentos do paciente
     */
    private List<AtendimentoResumoDTO> buscarUltimosAtendimentos(Integer idPaciente, Integer limite) {
        List<AtendiFisio> atendimentos = atendiFisioRepository.findByIdPacienteOrderByDataAtendiDesc(idPaciente);
        
        return atendimentos.stream()
            .limit(limite)
            .map(atd -> {
                AtendimentoResumoDTO resumo = new AtendimentoResumoDTO();
                resumo.setIdAtendiFisio(atd.getIdAtendiFisio());
                resumo.setDataAtendimento(atd.getDataAtendi());
                resumo.setDescricao(atd.getDescrAtendi());
                // TODO: Buscar nome do profissional e procedimento se necessário
                return resumo;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Busca exercícios prescritos ativos (com realizações nos últimos 30 dias)
     */
    private List<ExercicioPrescritoDTO> buscarExerciciosAtivos(Integer idPaciente) {
        // Buscar exercícios realizados recentemente
        // List<ExercRealizado> exerciciosRealizados = exercRealizadoRepository.findByPaciente(idPaciente);
        
        // Agrupar por prescrição e contar
        // Simplificado - em produção, usar GROUP BY na query
        // TODO: Implementar quando tiver relacionamento completo com ExercPresc
        return new ArrayList<>();
    }
    
    /**
     * Calcula evolução diária dos últimos N dias
     */
    private List<EvolucaoDiariaDTO> calcularEvolucaoRecente(Integer idPaciente, Integer dias) {
        LocalDate dataInicio = LocalDate.now().minusDays(dias);
        LocalDate dataFim = LocalDate.now();
        
        List<AtendiFisio> atendimentos = atendiFisioRepository.findByPacienteAndPeriodo(
            idPaciente, dataInicio, dataFim
        );
        
        // Agrupar por data
        return atendimentos.stream()
            .collect(Collectors.groupingBy(AtendiFisio::getDataAtendi))
            .entrySet().stream()
            .map(entry -> {
                EvolucaoDiariaDTO evolucao = new EvolucaoDiariaDTO();
                evolucao.setData(entry.getKey());
                evolucao.setQuantidadeAtendimentos(entry.getValue().size());
                
                // Concatenar resumos
                String resumo = entry.getValue().stream()
                    .map(AtendiFisio::getDescrAtendi)
                    .filter(d -> d != null && !d.isEmpty())
                    .limit(2)
                    .collect(Collectors.joining("; "));
                evolucao.setResumoDia(resumo);
                
                return evolucao;
            })
            .sorted((a, b) -> b.getData().compareTo(a.getData()))
            .collect(Collectors.toList());
    }
    
    /**
     * Cria uma nova evolução SOAP
     */
    @Transactional
    public Prontuario criarEvolucaoSOAP(AtendimentoSOAPDTO soapDTO) {
        // Validar paciente ativo
        if (!pacienteRepository.isPacienteAtivo(soapDTO.getIdPaciente())) {
            throw new IllegalStateException("Paciente inativo não pode ter evoluções");
        }
        
        // Criar prontuário com formato SOAP
        Prontuario prontuario = new Prontuario();
        prontuario.setIdPaciente(soapDTO.getIdPaciente());
        prontuario.setIdProfissio(soapDTO.getIdProfissio());
        prontuario.setIdEspec(soapDTO.getIdEspec());
        // prontuario.setIdProced(soapDTO.getIdProced()); // Removed - not available in DTO
        prontuario.setDataProced(soapDTO.getDataAtendimento());
        
        // Montar descrição em formato SOAP
        StringBuilder descricao = new StringBuilder();
        descricao.append("=== EVOLUÇÃO SOAP ===\n\n");
        
        if (soapDTO.getSubjetivo() != null && !soapDTO.getSubjetivo().isEmpty()) {
            descricao.append("[S] SUBJETIVO:\n")
                     .append(soapDTO.getSubjetivo())
                     .append("\n\n");
        }
        
        if (soapDTO.getObjetivo() != null && !soapDTO.getObjetivo().isEmpty()) {
            descricao.append("[O] OBJETIVO:\n")
                     .append(soapDTO.getObjetivo())
                     .append("\n\n");
        }
        
        if (soapDTO.getAvaliacao() != null && !soapDTO.getAvaliacao().isEmpty()) {
            descricao.append("[A] AVALIAÇÃO:\n")
                     .append(soapDTO.getAvaliacao())
                     .append("\n\n");
        }
        
        if (soapDTO.getPlano() != null && !soapDTO.getPlano().isEmpty()) {
            descricao.append("[P] PLANO:\n")
                     .append(soapDTO.getPlano())
                     .append("\n");
        }
        
        prontuario.setDescrProntu(descricao.toString());
        prontuario.setLinkProced(soapDTO.getLinkProced());
        prontuario.setAutoPacVisu(soapDTO.getAutoPacVisu() != null && soapDTO.getAutoPacVisu() ? 1 : 0);
        
        return prontuarioRepository.save(prontuario);
    }
}

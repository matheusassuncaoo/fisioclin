package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.DTOs.ProfissionalDTO;
import com.br.fasipe.fisioclin.Models.Profissional;
import com.br.fasipe.fisioclin.Repositories.PessoaFisRepository;
import com.br.fasipe.fisioclin.Repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de profissionais
 */
@Service
public class ProfissionalService {
    
    @Autowired
    private ProfissionalRepository profissionalRepository;
    
    @Autowired
    private PessoaFisRepository pessoaFisRepository;
    
    /**
     * Lista todos os profissionais ativos
     */
    @Transactional(readOnly = true)
    public List<ProfissionalDTO> listarProfissionaisAtivos() {
        List<Profissional> profissionais = profissionalRepository.findAll();
        return converterParaDTOList(profissionais.stream()
            .filter(p -> p.getStatusProfi() == Profissional.StatusProfi._1)
            .toList());
    }
    
    /**
     * Lista profissionais de fisioterapia (CREFITO)
     * Por enquanto retorna TODOS os profissionais para facilitar
     */
    @Transactional(readOnly = true)
    public List<ProfissionalDTO> listarProfissionaisFisioterapia() {
        List<ProfissionalDTO> resultado = new ArrayList<>();
        
        try {
            System.out.println("🔍 Listando TODOS os profissionais...");
            
            // Buscar TODOS os profissionais (sem filtro)
            List<Profissional> todosProfissionais = profissionalRepository.findAll();
            System.out.println("📋 Total de profissionais no banco: " + todosProfissionais.size());
            
            // Converter todos para DTO
            for (Profissional p : todosProfissionais) {
                try {
                    ProfissionalDTO dto = converterParaDTO(p);
                    if (dto != null && dto.getNomeProfissional() != null && !dto.getNomeProfissional().isEmpty()) {
                        resultado.add(dto);
                        System.out.println("✅ Profissional adicionado: " + dto.getNomeProfissional() + " (ID: " + dto.getIdProfissio() + ")");
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao converter profissional ID " + p.getIdProfissio() + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar profissionais: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📋 Total de profissionais retornados: " + resultado.size());
        
        return resultado;
    }
    
    /**
     * Busca profissional por ID
     */
    @Transactional(readOnly = true)
    public Optional<ProfissionalDTO> buscarPorId(Integer idProfissio) {
        return profissionalRepository.findById(idProfissio)
            .map(this::converterParaDTO);
    }
    
    /**
     * Busca nome do profissional por ID
     */
    @Transactional(readOnly = true)
    public String buscarNomeProfissional(Integer idProfissio) {
        if (idProfissio == null) return null;
        
        try {
            // Tenta primeiro com query JPQL
            return pessoaFisRepository.findNomeProfissional(idProfissio)
                .orElse("Profissional #" + idProfissio);
        } catch (Exception e) {
            // Se falhar, tenta com query nativa
            try {
                return pessoaFisRepository.findNomeProfissionalNative(idProfissio)
                    .orElse("Profissional #" + idProfissio);
            } catch (Exception e2) {
                System.err.println("⚠️ Erro ao buscar nome do profissional " + idProfissio + ": " + e2.getMessage());
                return "Profissional #" + idProfissio;
            }
        }
    }
    
    /**
     * Converte entidade para DTO
     */
    private ProfissionalDTO converterParaDTO(Profissional p) {
        if (p == null) return null;
        
        try {
            ProfissionalDTO dto = new ProfissionalDTO();
            dto.setIdProfissio(p.getIdProfissio());
            dto.setAtivo(true); // Assumir ativo por padrão
            
            // Buscar nome do profissional (com tratamento de erro)
            String nome = null;
            try {
                nome = buscarNomeProfissional(p.getIdProfissio());
                System.out.println("    📝 Nome encontrado para ID " + p.getIdProfissio() + ": " + nome);
            } catch (Exception e) {
                System.err.println("    ⚠️ Erro ao buscar nome do profissional " + p.getIdProfissio() + ": " + e.getMessage());
                // Não quebra, continua com nome padrão
            }
            
            // Se não encontrar nome válido, usa ID como fallback (melhor que não mostrar nada)
            if (nome == null || nome.isEmpty() || nome.startsWith("Profissional #")) {
                System.out.println("    ⚠️ Nome não encontrado, usando ID como fallback: " + p.getIdProfissio());
                nome = "Profissional #" + p.getIdProfissio();
            }
            
            dto.setNomeProfissional(nome);
            
            // Conselho e especialidade padrão
            dto.setConselho("CREFITO");
            dto.setEspecialidade("Fisioterapia");
            
            return dto;
        } catch (Exception e) {
            System.err.println("❌ Erro ao converter profissional: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converte lista de entidades para lista de DTOs
     */
    private List<ProfissionalDTO> converterParaDTOList(List<Profissional> profissionais) {
        List<ProfissionalDTO> resultado = new ArrayList<>();
        for (Profissional p : profissionais) {
            ProfissionalDTO dto = converterParaDTO(p);
            if (dto != null) {
                resultado.add(dto);
            }
        }
        return resultado;
    }
}


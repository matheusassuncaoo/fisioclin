package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.ExercRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExercRealizadoRepository extends JpaRepository<ExercRealizado, Integer> {
    
    // Buscar exercícios realizados por prescrição
    List<ExercRealizado> findByIdExercPrescOrderByDataHoraDesc(Integer idExercPresc);
    
    // Buscar exercícios realizados em um período (EVOLUÇÃO)
    @Query("SELECT er FROM ExercRealizado er WHERE er.idExercPresc = :idExercPresc AND er.dataHora BETWEEN :dataInicio AND :dataFim ORDER BY er.dataHora DESC")
    List<ExercRealizado> findByPrescricaoAndPeriodo(
        @Param("idExercPresc") Integer idExercPresc,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
    
    // Buscar exercícios realizados por paciente através da prescrição
    @Query("SELECT er FROM ExercRealizado er " +
           "JOIN ExercPresc ep ON er.idExercPresc = ep.idExercPresc " +
           "JOIN Anamnese a ON ep.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente " +
           "ORDER BY er.dataHora DESC")
    List<ExercRealizado> findByPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Contar exercícios realizados por prescrição
    Long countByIdExercPresc(Integer idExercPresc);
    
    // Últimos exercícios realizados do paciente
    @Query("SELECT er FROM ExercRealizado er " +
           "JOIN ExercPresc ep ON er.idExercPresc = ep.idExercPresc " +
           "JOIN Anamnese a ON ep.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente " +
           "ORDER BY er.dataHora DESC " +
           "LIMIT :limit")
    List<ExercRealizado> findUltimosExerciciosPaciente(
        @Param("idPaciente") Integer idPaciente,
        @Param("limit") Integer limit
    );
    
    // ===== QUERIES COM DETALHES DO EXERCÍCIO =====
    
    // Buscar exercícios realizados com detalhes completos (para exibição)
    @Query("SELECT er.idExercRealizado, e.descrExerc, ep.qtdExerc, ep.orientacao, er.dataHora, er.observacao " +
           "FROM ExercRealizado er " +
           "JOIN ExercPresc ep ON er.idExercPresc = ep.idExercPresc " +
           "JOIN Exercicio e ON ep.idExercicio = e.idExercicio " +
           "JOIN Anamnese a ON ep.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente " +
           "ORDER BY er.dataHora DESC")
    List<Object[]> findExerciciosComDetalhesByPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Contar exercícios realizados por paciente
    @Query("SELECT COUNT(er) FROM ExercRealizado er " +
           "JOIN ExercPresc ep ON er.idExercPresc = ep.idExercPresc " +
           "JOIN Anamnese a ON ep.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente")
    Long countByPaciente(@Param("idPaciente") Integer idPaciente);
}

package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.ExercPresc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercPrescRepository extends JpaRepository<ExercPresc, Integer> {
    
    // Buscar prescrições por anamnese
    List<ExercPresc> findByIdAnamnese(Integer idAnamnese);
    
    // Buscar prescrições por exercício
    List<ExercPresc> findByIdExercicio(Integer idExercicio);
    
    // Buscar prescrições por paciente (através da anamnese)
    @Query("SELECT ep FROM ExercPresc ep " +
           "JOIN Anamnese a ON ep.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente")
    List<ExercPresc> findByPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Buscar prescrições ativas com detalhes de exercício
    @Query("SELECT ep FROM ExercPresc ep " +
           "JOIN Exercicio e ON ep.idExercicio = e.idExercicio " +
           "WHERE ep.idAnamnese = :idAnamnese")
    List<ExercPresc> findPrescricoesComExercicio(@Param("idAnamnese") Integer idAnamnese);
}

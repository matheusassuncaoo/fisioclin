package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Integer> {
    
    // Buscar por descrição (like)
    @Query("SELECT e FROM Exercicio e WHERE LOWER(e.descrExerc) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Exercicio> findByDescricaoContaining(@Param("descricao") String descricao);
    
    // Buscar exercícios com vídeo
    @Query("SELECT e FROM Exercicio e WHERE e.linkVideo IS NOT NULL AND e.linkVideo <> ''")
    List<Exercicio> findExerciciosComVideo();
    
    // Buscar exercícios sem vídeo
    @Query("SELECT e FROM Exercicio e WHERE e.linkVideo IS NULL OR e.linkVideo = ''")
    List<Exercicio> findExerciciosSemVideo();
}

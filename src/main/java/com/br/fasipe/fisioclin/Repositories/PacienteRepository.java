package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Repository seguindo padrão MVC
 * Apenas queries simples - lógica complexa fica no Service
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    
    // Buscar por RG
    Optional<Paciente> findByRgPaciente(String rgPaciente);
    
    // Buscar por documento
    Optional<Paciente> findByIdDocumento(BigInteger idDocumento);
    
    // Listar por status
    List<Paciente> findByStatusPac(Integer statusPac);
    
    // Contar pacientes ativos (statusPac como Integer)
    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.statusPac = 1")
    Long countPacientesAtivos();
    
    // Contar por status (usando Integer: 1=ativo, 0=inativo)
    Long countByStatusPac(Integer statusPac);
    
    // Verificar se paciente está ativo
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Paciente p WHERE p.idPaciente = :id AND p.statusPac = 1")
    Boolean isPacienteAtivo(@Param("id") Integer id);
}

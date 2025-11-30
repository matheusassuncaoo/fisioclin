package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Integer> {
    
    // Verificar se profissional existe
    boolean existsByIdProfissio(Integer idProfissio);
    
    // Buscar profissionais por conselho profissional (IDCONSEPROFI)
    List<Profissional> findByIdConseProfi(Integer idConseProfi);
    
    // Buscar profissionais ativos por conselho
    @Query("SELECT p FROM Profissional p WHERE p.idConseProfi = :idConseProfi AND p.statusProfi = '_1'")
    List<Profissional> findAtivosByConseProfi(@Param("idConseProfi") Integer idConseProfi);
    
    // Buscar profissionais de fisioterapia (CREFITO)
    @Query("SELECT p FROM Profissional p " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%'")
    List<Profissional> findProfissionaisFisioterapia();
    
    // Buscar profissionais ativos de fisioterapia
    @Query("SELECT p FROM Profissional p " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE (UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%') " +
           "AND p.statusProfi = '_1'")
    List<Profissional> findProfissionaisFisioterapiaAtivos();
}


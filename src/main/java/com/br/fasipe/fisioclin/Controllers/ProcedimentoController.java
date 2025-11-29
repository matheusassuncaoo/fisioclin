package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.Especialidade;
import com.br.fasipe.fisioclin.Models.Procedimento;
import com.br.fasipe.fisioclin.Repositories.EspecProcedRepository;
import com.br.fasipe.fisioclin.Repositories.EspecialidadeRepository;
import com.br.fasipe.fisioclin.Repositories.ProcedimentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procedimentos")
@Tag(name = "Procedimentos", description = "API de gerenciamento de procedimentos")
public class ProcedimentoController {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;
    
    @Autowired
    private EspecProcedRepository especProcedRepository;
    
    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @GetMapping
    @Operation(summary = "Listar todos os procedimentos")
    public ResponseEntity<List<Procedimento>> listarTodos() {
        return ResponseEntity.ok(procedimentoRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar procedimento por ID")
    public ResponseEntity<Procedimento> buscarPorId(@PathVariable Integer id) {
        return procedimentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar procedimento por código")
    public ResponseEntity<Procedimento> buscarPorCodigo(@PathVariable String codigo) {
        return procedimentoRepository.findByCodProced(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar procedimentos por descrição")
    public ResponseEntity<List<Procedimento>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(procedimentoRepository.findByDescricaoContaining(descricao));
    }

    /**
     * Endpoint principal para buscar procedimentos de fisioterapia
     * Código da especialidade fisioterapia: 30
     */
    @GetMapping("/fisioterapia")
    @Operation(summary = "Listar procedimentos de fisioterapia")
    public ResponseEntity<List<Procedimento>> listarProcedimentosFisioterapia() {
        List<Procedimento> procedimentos = especProcedRepository
                .findProcedimentosByCodEspec(Especialidade.COD_FISIOTERAPIA);
        
        if (procedimentos.isEmpty()) {
            // Fallback: se não houver vínculo, buscar por descrição contendo "fisio"
            procedimentos = procedimentoRepository.findByDescricaoContaining("fisio");
        }
        
        return ResponseEntity.ok(procedimentos);
    }

    /**
     * Buscar procedimentos por código de especialidade
     * @param codEspec Código da especialidade (00=Biomedicina, 10=Enfermagem, 20=Farmácia, 30=Fisioterapia, etc.)
     */
    @GetMapping("/especialidade/{codEspec}")
    @Operation(summary = "Listar procedimentos por código de especialidade")
    public ResponseEntity<List<Procedimento>> listarPorEspecialidade(@PathVariable String codEspec) {
        List<Procedimento> procedimentos = especProcedRepository.findProcedimentosByCodEspec(codEspec);
        return ResponseEntity.ok(procedimentos);
    }

    /**
     * Listar todas as especialidades disponíveis
     */
    @GetMapping("/especialidades")
    @Operation(summary = "Listar todas as especialidades")
    public ResponseEntity<List<Especialidade>> listarEspecialidades() {
        return ResponseEntity.ok(especialidadeRepository.findAll());
    }
}


package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.ExercRealizado;
import com.br.fasipe.fisioclin.Repositories.ExercRealizadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExercRealizadoService {
    
    @Autowired
    private ExercRealizadoRepository exercRealizadoRepository;
    
    @Transactional(readOnly = true)
    public List<ExercRealizado> listarTodos() {
        return exercRealizadoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<ExercRealizado> buscarPorId(Integer id) {
        return exercRealizadoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ExercRealizado> buscarPorPrescricao(Integer idExercPresc) {
        return exercRealizadoRepository.findByIdExercPrescOrderByDataHoraDesc(idExercPresc);
    }
    
    @Transactional(readOnly = true)
    public List<ExercRealizado> buscarPorPaciente(Integer idPaciente) {
        return exercRealizadoRepository.findByPaciente(idPaciente);
    }
    
    @Transactional(readOnly = true)
    public List<ExercRealizado> buscarEvolucaoExercicios(Integer idExercPresc, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return exercRealizadoRepository.findByPrescricaoAndPeriodo(idExercPresc, dataInicio, dataFim);
    }
    
    @Transactional(readOnly = true)
    public List<ExercRealizado> buscarUltimosExercicios(Integer idPaciente, Integer limite) {
        return exercRealizadoRepository.findUltimosExerciciosPaciente(idPaciente, limite);
    }
    
    @Transactional(readOnly = true)
    public Long contarExerciciosRealizados(Integer idExercPresc) {
        return exercRealizadoRepository.countByIdExercPresc(idExercPresc);
    }
    
    @Transactional
    public ExercRealizado registrar(ExercRealizado exercRealizado) {
        // Validações
        if (exercRealizado.getIdExercPresc() == null) {
            throw new IllegalArgumentException("ID da prescrição de exercício é obrigatório");
        }
        
        // Define data/hora atual se não informada
        if (exercRealizado.getDataHora() == null) {
            exercRealizado.setDataHora(LocalDateTime.now());
        }
        
        return exercRealizadoRepository.save(exercRealizado);
    }
    
    @Transactional
    public ExercRealizado atualizar(Integer id, ExercRealizado exercRealizado) {
        Optional<ExercRealizado> existente = exercRealizadoRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Exercício realizado não encontrado");
        }
        
        exercRealizado.setIdExercRealizado(id);
        return exercRealizadoRepository.save(exercRealizado);
    }
    
    @Transactional
    public void deletar(Integer id) {
        if (!exercRealizadoRepository.existsById(id)) {
            throw new IllegalArgumentException("Exercício realizado não encontrado");
        }
        exercRealizadoRepository.deleteById(id);
    }
}

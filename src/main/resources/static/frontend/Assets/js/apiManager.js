/**
 * API Manager - Gerenciador de requisições para a API do FisioClin
 * Centraliza todas as chamadas HTTP para a API REST
 */

const API_BASE_URL = 'http://localhost:8080/api';

class ApiManager {
    
    /**
     * Método genérico para fazer requisições HTTP
     */
    async request(endpoint, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // Para incluir cookies de autenticação
        };

        const config = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers,
            },
        };

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
            
            // Se a resposta for 204 (No Content), retorna null
            if (response.status === 204) {
                return null;
            }

            // Se não for sucesso, lança erro
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Erro HTTP: ${response.status}`);
            }

            // Retorna os dados em JSON
            return await response.json();
        } catch (error) {
            console.error('Erro na requisição:', error);
            throw error;
        }
    }

    // ===== PACIENTES =====
    
    async listarPacientes() {
        return this.request('/pacientes');
    }

    async listarPacientesAtivos() {
        return this.request('/pacientes/ativos');
    }

    async buscarPacientePorId(id) {
        return this.request(`/pacientes/${id}`);
    }

    async buscarPacientePorRg(rg) {
        return this.request(`/pacientes/rg/${rg}`);
    }

    async criarPaciente(paciente) {
        return this.request('/pacientes', {
            method: 'POST',
            body: JSON.stringify(paciente),
        });
    }

    async atualizarPaciente(id, paciente) {
        return this.request(`/pacientes/${id}`, {
            method: 'PUT',
            body: JSON.stringify(paciente),
        });
    }

    async verificarPacienteAtivo(id) {
        return this.request(`/pacientes/${id}/ativo`);
    }

    async contarPacientesAtivos() {
        return this.request('/pacientes/count/ativos');
    }

    // ===== ATENDIMENTOS DE FISIOTERAPIA =====

    async listarAtendimentos() {
        return this.request('/atendimentos');
    }

    async buscarAtendimentoPorId(id) {
        return this.request(`/atendimentos/${id}`);
    }

    async buscarAtendimentosPorPaciente(idPaciente) {
        return this.request(`/atendimentos/paciente/${idPaciente}`);
    }

    async buscarAtendimentosPorProfissional(idProfissio) {
        return this.request(`/atendimentos/profissional/${idProfissio}`);
    }

    async buscarAtendimentosPorPeriodo(dataInicio, dataFim) {
        return this.request(`/atendimentos/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`);
    }

    async buscarEvolucaoPaciente(idPaciente, dataInicio, dataFim) {
        return this.request(`/atendimentos/evolucao/${idPaciente}?dataInicio=${dataInicio}&dataFim=${dataFim}`);
    }

    async buscarUltimoAtendimento(idPaciente) {
        return this.request(`/atendimentos/paciente/${idPaciente}/ultimo`);
    }

    async contarAtendimentos(idPaciente) {
        return this.request(`/atendimentos/paciente/${idPaciente}/count`);
    }

    async criarAtendimento(atendimento) {
        return this.request('/atendimentos', {
            method: 'POST',
            body: JSON.stringify(atendimento),
        });
    }

    async atualizarAtendimento(id, atendimento) {
        return this.request(`/atendimentos/${id}`, {
            method: 'PUT',
            body: JSON.stringify(atendimento),
        });
    }

    async deletarAtendimento(id) {
        return this.request(`/atendimentos/${id}`, {
            method: 'DELETE',
        });
    }

    // ===== PRONTUÁRIOS =====

    async listarProntuarios() {
        return this.request('/prontuarios');
    }

    async buscarProntuarioPorId(id) {
        return this.request(`/prontuarios/${id}`);
    }

    async buscarProntuariosPorPaciente(idPaciente) {
        return this.request(`/prontuarios/paciente/${idPaciente}`);
    }

    async criarProntuario(prontuario) {
        return this.request('/prontuarios', {
            method: 'POST',
            body: JSON.stringify(prontuario),
        });
    }

    async atualizarProntuario(id, prontuario) {
        return this.request(`/prontuarios/${id}`, {
            method: 'PUT',
            body: JSON.stringify(prontuario),
        });
    }

    // ===== EXERCÍCIOS REALIZADOS =====

    async listarExerciciosRealizados() {
        return this.request('/exercicios-realizados');
    }

    async buscarExercicioRealizadoPorId(id) {
        return this.request(`/exercicios-realizados/${id}`);
    }

    async buscarExerciciosRealizadosPorPaciente(idPaciente) {
        return this.request(`/exercicios-realizados/paciente/${idPaciente}`);
    }

    async criarExercicioRealizado(exercicio) {
        return this.request('/exercicios-realizados', {
            method: 'POST',
            body: JSON.stringify(exercicio),
        });
    }

    async atualizarExercicioRealizado(id, exercicio) {
        return this.request(`/exercicios-realizados/${id}`, {
            method: 'PUT',
            body: JSON.stringify(exercicio),
        });
    }

    async deletarExercicioRealizado(id) {
        return this.request(`/exercicios-realizados/${id}`, {
            method: 'DELETE',
        });
    }
}

// Criar instância global do ApiManager
const api = new ApiManager();

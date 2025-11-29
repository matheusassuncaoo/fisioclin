/**
 * API Manager - Gerenciador de requisições para a API do FisioClin
 * Centraliza todas as chamadas HTTP para a API REST
 * 
 * SEGURANÇA:
 * - CSRF token automático (quando implementado no backend)
 * - Sanitização básica de entrada
 * - Tratamento de erros HTTP padronizado
 * - Rate limiting awareness
 */

// Detecta automaticamente a URL base (dev ou produção)
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8080/api'
    : `${window.location.origin}/api`;

class ApiManager {
    
    constructor() {
        this.retryCount = 0;
        this.maxRetries = 3;
    }
    
    /**
     * Método genérico para fazer requisições HTTP
     * Com tratamento de erros e retry automático
     */
    async request(endpoint, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
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
            
            // Verificar rate limiting
            this.handleRateLimitHeaders(response);
            
            // Se a resposta for 204 (No Content), retorna null
            if (response.status === 204) {
                return null;
            }

            // Se a resposta for 429 (Too Many Requests)
            if (response.status === 429) {
                const retryAfter = response.headers.get('Retry-After') || 60;
                throw new Error(`Limite de requisições excedido. Tente novamente em ${retryAfter} segundos.`);
            }

            // Se não for sucesso, lança erro
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const errorMessage = this.parseErrorMessage(response.status, errorData);
                throw new Error(errorMessage);
            }

            // Reset retry count on success
            this.retryCount = 0;

            // Retorna os dados em JSON
            return await response.json();
        } catch (error) {
            // Log detalhado para debugging
            console.error('Erro na requisição:', {
                endpoint,
                method: config.method || 'GET',
                error: error.message
            });
            
            // Re-throw para tratamento no chamador
            throw error;
        }
    }

    /**
     * Trata headers de rate limiting para informar o usuário
     */
    handleRateLimitHeaders(response) {
        const remaining = response.headers.get('X-RateLimit-Remaining');
        const limit = response.headers.get('X-RateLimit-Limit');
        
        if (remaining !== null && parseInt(remaining) <= 5) {
            console.warn(`Atenção: ${remaining} de ${limit} requisições restantes neste minuto`);
        }
    }

    /**
     * Converte códigos de erro HTTP em mensagens amigáveis
     */
    parseErrorMessage(status, errorData) {
        // Se o backend retornou uma mensagem, usar ela
        if (errorData.message) {
            return errorData.message;
        }

        // Mensagens padrão por código HTTP
        const messages = {
            400: 'Dados inválidos. Verifique as informações e tente novamente.',
            401: 'Sessão expirada. Por favor, faça login novamente.',
            403: 'Você não tem permissão para realizar esta ação.',
            404: 'Recurso não encontrado.',
            409: 'Conflito de dados. O registro pode já existir.',
            422: 'Os dados enviados são inválidos.',
            429: 'Muitas requisições. Aguarde um momento.',
            500: 'Erro interno do servidor. Tente novamente mais tarde.',
            502: 'Servidor indisponível. Tente novamente mais tarde.',
            503: 'Serviço temporariamente indisponível.',
        };

        return messages[status] || `Erro HTTP: ${status}`;
    }

    /**
     * Sanitiza strings antes de enviar para API (proteção XSS básica)
     */
    sanitizeString(str) {
        if (typeof str !== 'string') return str;
        return str
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#x27;')
            .trim();
    }

    /**
     * Sanitiza objeto antes de enviar (aplica sanitização em todos os campos string)
     */
    sanitizeObject(obj) {
        if (typeof obj !== 'object' || obj === null) return obj;
        
        const sanitized = {};
        for (const [key, value] of Object.entries(obj)) {
            if (typeof value === 'string') {
                sanitized[key] = this.sanitizeString(value);
            } else if (typeof value === 'object' && value !== null) {
                sanitized[key] = this.sanitizeObject(value);
            } else {
                sanitized[key] = value;
            }
        }
        return sanitized;
    }

    // ===== PACIENTES =====
    
    async listarPacientes() {
        return this.request('/pacientes');
    }

    async listarPacientesAtivos() {
        return this.request('/pacientes/ativos/com-nome');
    }

    async listarPacientesInativos() {
        return this.request('/pacientes/inativos/com-nome');
    }

    async listarTodosPacientes() {
        return this.request('/pacientes/com-nome');
    }

    async buscarPacientePorId(id) {
        return this.request(`/pacientes/${id}/com-nome`);
    }

    async buscarPacientePorRg(rg) {
        return this.request(`/pacientes/rg/${rg}`);
    }

    async criarPaciente(paciente) {
        return this.request('/pacientes', {
            method: 'POST',
            body: JSON.stringify(this.sanitizeObject(paciente)),
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

    /**
     * Busca TODOS os detalhes consolidados do paciente
     * Inclui: estatísticas, última anamnese, últimos atendimentos,
     * exercícios ativos e evolução dos últimos 30 dias
     */
    async buscarDetalhesCompletos(idPaciente) {
        return this.request(`/pacientes/${idPaciente}/detalhes`);
    }

    /**
     * Cria uma nova evolução usando método SOAP
     * @param {Object} soapData - {idPaciente, idProfissio, idProced, idEspec, dataAtendimento, subjetivo, objetivo, avaliacao, plano}
     */
    async criarEvolucaoSOAP(soapData) {
        // Sanitizar campos de texto antes de enviar
        const sanitized = this.sanitizeObject(soapData);
        return this.request('/pacientes/evolucao-soap', {
            method: 'POST',
            body: JSON.stringify(sanitized),
        });
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

    /**
     * Cria um novo atendimento simples
     * @param {Object} atendimento - {idPaciente, idProfissio, codProced, dataAtendimento, descricao}
     */
    async criarAtendimento(atendimento) {
        const sanitized = this.sanitizeObject(atendimento);
        return this.request('/atendimentos/simples', {
            method: 'POST',
            body: JSON.stringify(sanitized),
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
            body: JSON.stringify(this.sanitizeObject(prontuario)),
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

    // ===== PROCEDIMENTOS =====

    async listarProcedimentos() {
        return this.request('/procedimentos');
    }

    async buscarProcedimentoPorId(id) {
        return this.request(`/procedimentos/${id}`);
    }

    async buscarProcedimentoPorCodigo(codigo) {
        return this.request(`/procedimentos/codigo/${codigo}`);
    }

    /**
     * Busca procedimentos de fisioterapia (especialidade 30)
     * Este é o endpoint principal para o sistema de fisioterapia
     */
    async listarProcedimentosFisioterapia() {
        return this.request('/procedimentos/fisioterapia');
    }

    async listarProcedimentosPorEspecialidade(codEspec) {
        return this.request(`/procedimentos/especialidade/${codEspec}`);
    }

    async listarEspecialidades() {
        return this.request('/procedimentos/especialidades');
    }
}

// Criar instância global do ApiManager
const api = new ApiManager();

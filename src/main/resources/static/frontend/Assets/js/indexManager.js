/**
 * Index Manager - Gerencia a lógica da aplicação de evolução de pacientes
 */

class EvolutionManager {
    constructor() {
        this.currentPatient = null;
        this.currentAtendimentos = [];
        this.editingAtendimento = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadPacientes();
    }

    setupEventListeners() {
        // Busca de paciente
        document.getElementById('searchBtn').addEventListener('click', () => this.searchPatient());
        document.getElementById('patientSelect').addEventListener('change', (e) => {
            if (e.target.value) {
                this.loadPatientData(e.target.value);
            }
        });

        // Formulário de evolução
        document.getElementById('evolutionForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveEvolution();
        });

        document.getElementById('cancelBtn').addEventListener('click', () => this.cancelEvolution());

        // Filtros de período
        document.getElementById('filterBtn').addEventListener('click', () => this.filterTimeline());
        document.getElementById('clearFilterBtn').addEventListener('click', () => this.clearFilters());

        // Enter no select de paciente
        document.getElementById('patientSelect').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                const selectedId = e.target.value;
                if (selectedId) {
                    this.loadPatientData(selectedId);
                }
            }
        });
    }

    async loadPacientes() {
        try {
            const pacientes = await api.listarPacientesAtivos();
            this.populatePacienteSelect(pacientes);
        } catch (error) {
            this.showMessage('Erro ao carregar lista de pacientes', 'error');
            console.error(error);
        }
    }

    populatePacienteSelect(pacientes) {
        const select = document.getElementById('patientSelect');
        select.innerHTML = '<option value="">Selecione um paciente...</option>';
        
        pacientes.forEach(paciente => {
            const option = document.createElement('option');
            option.value = paciente.idPaciente;
            option.textContent = `${paciente.idPaciente} - RG: ${paciente.rgPaciente}`;
            select.appendChild(option);
        });
    }

    async searchPatient() {
        const patientId = document.getElementById('patientSelect').value;
        
        if (!patientId) {
            this.showMessage('Por favor, selecione um paciente', 'error');
            return;
        }

        await this.loadPatientData(patientId);
    }

    async loadPatientData(patientId) {
        this.showLoading(true);
        
        try {
            const [paciente, atendimentos, totalAtendimentos] = await Promise.all([
                api.buscarPacientePorId(patientId),
                api.buscarAtendimentosPorPaciente(patientId),
                api.contarAtendimentos(patientId)
            ]);

            this.currentPatient = paciente;
            this.currentAtendimentos = atendimentos;

            this.displayPatientInfo(paciente, totalAtendimentos);
            this.displayTimeline(atendimentos);
            this.showSections();
            this.showMessage('Paciente carregado com sucesso!', 'success');
        } catch (error) {
            this.showMessage('Erro ao carregar dados do paciente', 'error');
            console.error(error);
        } finally {
            this.showLoading(false);
        }
    }

    displayPatientInfo(paciente, totalAtendimentos) {
        const patientInfo = document.getElementById('patientInfo');
        const lastAtendimento = this.currentAtendimentos.length > 0 
            ? this.currentAtendimentos[0].dataAtendi 
            : 'Nenhum atendimento';

        patientInfo.innerHTML = `
            <div class="patient-header">
                <div class="patient-details">
                    <h3>Paciente ID: ${paciente.idPaciente}</h3>
                    <p><strong>RG:</strong> ${paciente.rgPaciente}</p>
                    <p><strong>Estado RG:</strong> ${paciente.estdoRgPac || 'N/A'}</p>
                    <p>
                        <strong>Status:</strong> 
                        <span class="badge ${paciente.statusPac ? 'badge-success' : 'badge-danger'}">
                            ${paciente.statusPac ? 'Ativo' : 'Inativo'}
                        </span>
                    </p>
                </div>
            </div>
            
            <div class="patient-stats">
                <div class="stat-card">
                    <label>Total de Atendimentos</label>
                    <div class="value">${totalAtendimentos}</div>
                </div>
                <div class="stat-card" style="border-left-color: var(--secondary-color)">
                    <label>Último Atendimento</label>
                    <div class="value" style="font-size: 1rem;">${this.formatDate(lastAtendimento)}</div>
                </div>
                <div class="stat-card" style="border-left-color: var(--warning-color)">
                    <label>ID Pessoa Física</label>
                    <div class="value">${paciente.idPessoaFis}</div>
                </div>
            </div>
        `;
    }

    displayTimeline(atendimentos) {
        const timeline = document.getElementById('timeline');
        
        if (!atendimentos || atendimentos.length === 0) {
            timeline.innerHTML = `
                <div class="empty-state">
                    Nenhum atendimento registrado para este paciente
                </div>
            `;
            return;
        }

        // Ordenar por data decrescente (mais recente primeiro)
        const sortedAtendimentos = [...atendimentos].sort((a, b) => 
            new Date(b.dataAtendi) - new Date(a.dataAtendi)
        );

        timeline.innerHTML = sortedAtendimentos.map(atendimento => `
            <div class="timeline-item" data-id="${atendimento.idAtendiFisio}">
                <div class="timeline-item-header">
                    <div class="timeline-date">${this.formatDate(atendimento.dataAtendi)}</div>
                    <div class="timeline-actions">
                        <button class="btn btn-small btn-primary" onclick="evolutionManager.editEvolution(${atendimento.idAtendiFisio})">
                            <i data-feather="edit-2"></i>
                            Editar
                        </button>
                        <button class="btn btn-small btn-danger" onclick="evolutionManager.deleteEvolution(${atendimento.idAtendiFisio})">
                            <i data-feather="trash-2"></i>
                            Excluir
                        </button>
                    </div>
                </div>
                <div class="timeline-description">
                    ${atendimento.descrAtendi || '<em>Sem descrição</em>'}
                </div>
                <div class="timeline-meta">
                    <span><strong>ID:</strong> ${atendimento.idAtendiFisio}</span>
                    <span><strong>Profissional:</strong> ${atendimento.idProfissio}</span>
                    <span><strong>Procedimento:</strong> ${atendimento.idProced}</span>
                </div>
            </div>
        `).join('');
        
        // Renderizar ícones Feather
        feather.replace();
    }

    showSections() {
        document.getElementById('patientInfo').classList.add('active');
        document.getElementById('evolutionForm').classList.add('active');
        document.getElementById('timelineSection').classList.add('active');
    }

    async saveEvolution() {
        if (!this.currentPatient) {
            this.showMessage('Nenhum paciente selecionado', 'error');
            return;
        }

        const formData = {
            idPaciente: this.currentPatient.idPaciente,
            idProfissio: parseInt(document.getElementById('idProfissio').value),
            idProced: parseInt(document.getElementById('idProced').value),
            dataAtendi: document.getElementById('dataAtendi').value,
            descrAtendi: document.getElementById('descrAtendi').value
        };

        // Validações
        if (!formData.idProfissio || !formData.idProced || !formData.dataAtendi) {
            this.showMessage('Por favor, preencha todos os campos obrigatórios', 'error');
            return;
        }

        this.showLoading(true);

        try {
            let result;
            if (this.editingAtendimento) {
                // Atualizar atendimento existente
                result = await api.atualizarAtendimento(this.editingAtendimento, formData);
                this.showMessage('Evolução atualizada com sucesso!', 'success');
            } else {
                // Criar novo atendimento
                result = await api.criarAtendimento(formData);
                this.showMessage('Evolução registrada com sucesso!', 'success');
            }

            // Recarregar dados do paciente
            await this.loadPatientData(this.currentPatient.idPaciente);
            this.resetForm();
        } catch (error) {
            this.showMessage('Erro ao salvar evolução: ' + error.message, 'error');
            console.error(error);
        } finally {
            this.showLoading(false);
        }
    }

    async editEvolution(idAtendimento) {
        const atendimento = this.currentAtendimentos.find(a => a.idAtendiFisio === idAtendimento);
        
        if (!atendimento) {
            this.showMessage('Atendimento não encontrado', 'error');
            return;
        }

        this.editingAtendimento = idAtendimento;
        
        // Preencher formulário
        document.getElementById('idProfissio').value = atendimento.idProfissio;
        document.getElementById('idProced').value = atendimento.idProced;
        document.getElementById('dataAtendi').value = atendimento.dataAtendi;
        document.getElementById('descrAtendi').value = atendimento.descrAtendi || '';

        // Mudar texto do botão
        const submitBtn = document.querySelector('#evolutionForm button[type="submit"]');
        submitBtn.innerHTML = '<i data-feather="edit"></i> Atualizar';
        feather.replace();
        
        // Scroll para o formulário
        document.getElementById('evolutionForm').scrollIntoView({ behavior: 'smooth' });
        
        this.showMessage('Editando evolução. Faça as alterações e clique em Atualizar.', 'info');
    }

    async deleteEvolution(idAtendimento) {
        if (!confirm('Tem certeza que deseja excluir esta evolução?')) {
            return;
        }

        this.showLoading(true);

        try {
            await api.deletarAtendimento(idAtendimento);
            this.showMessage('Evolução excluída com sucesso!', 'success');
            
            // Recarregar dados do paciente
            await this.loadPatientData(this.currentPatient.idPaciente);
        } catch (error) {
            this.showMessage('Erro ao excluir evolução: ' + error.message, 'error');
            console.error(error);
        } finally {
            this.showLoading(false);
        }
    }

    async filterTimeline() {
        const dataInicio = document.getElementById('dataInicio').value;
        const dataFim = document.getElementById('dataFim').value;

        if (!dataInicio || !dataFim) {
            this.showMessage('Por favor, selecione data de início e fim', 'error');
            return;
        }

        if (!this.currentPatient) {
            this.showMessage('Nenhum paciente selecionado', 'error');
            return;
        }

        this.showLoading(true);

        try {
            const evolucao = await api.buscarEvolucaoPaciente(
                this.currentPatient.idPaciente,
                dataInicio,
                dataFim
            );
            
            this.displayTimeline(evolucao);
            this.showMessage(`Mostrando ${evolucao.length} atendimento(s) no período selecionado`, 'info');
        } catch (error) {
            this.showMessage('Erro ao filtrar evolução: ' + error.message, 'error');
            console.error(error);
        } finally {
            this.showLoading(false);
        }
    }

    async clearFilters() {
        document.getElementById('dataInicio').value = '';
        document.getElementById('dataFim').value = '';
        
        if (this.currentPatient) {
            await this.loadPatientData(this.currentPatient.idPaciente);
        }
    }

    cancelEvolution() {
        this.resetForm();
        this.showMessage('Operação cancelada', 'info');
    }

    resetForm() {
        document.getElementById('evolutionFormElement').reset();
        this.editingAtendimento = null;
        const submitBtn = document.querySelector('#evolutionForm button[type="submit"]');
        submitBtn.innerHTML = '<i data-feather="save"></i> Salvar';
        feather.replace();
        
        // Definir data atual como padrão
        document.getElementById('dataAtendi').value = new Date().toISOString().split('T')[0];
    }

    showMessage(message, type = 'info') {
        const messageDiv = document.getElementById('message');
        messageDiv.className = `message message-${type} active`;
        messageDiv.textContent = message;

        // Auto-esconder após 5 segundos
        setTimeout(() => {
            messageDiv.classList.remove('active');
        }, 5000);
    }

    showLoading(show) {
        const loading = document.getElementById('loading');
        if (show) {
            loading.classList.add('active');
        } else {
            loading.classList.remove('active');
        }
    }

    formatDate(dateString) {
        if (!dateString) return 'N/A';
        
        const date = new Date(dateString + 'T00:00:00');
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }
}

// Inicializar quando o DOM estiver pronto
let evolutionManager;
document.addEventListener('DOMContentLoaded', () => {
    evolutionManager = new EvolutionManager();
    
    // Definir data atual como padrão no formulário
    document.getElementById('dataAtendi').value = new Date().toISOString().split('T')[0];
});

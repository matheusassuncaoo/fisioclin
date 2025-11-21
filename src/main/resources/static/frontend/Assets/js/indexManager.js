/**
 * IndexManager.js - Sistema de Gestão Fisioclin
 */

class FisioclinApp {
    constructor() {
        this.allPacientes = [];
        this.currentPatient = null;
        this.currentAtendimentos = [];
        
        this.init();
    }

    async init() {
        this.setupEventListeners();
        await this.loadPacientes();
        
        // Set today's date
        const dateInput = document.getElementById('inputDate');
        if (dateInput) {
            dateInput.valueAsDate = new Date();
        }
        
        feather.replace();
    }

    setupEventListeners() {
        // Navigation Menu
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                this.changePage(item.dataset.page);
            });
        });

        // Search & Filter
        const searchInput = document.getElementById('searchPaciente');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.filterPacientes(e.target.value));
        }

        const filterStatus = document.getElementById('filterStatus');
        if (filterStatus) {
            filterStatus.addEventListener('change', () => this.loadPacientes());
        }

        // Modal Controls
        const btnCloseModal = document.getElementById('btnCloseModal');
        if (btnCloseModal) {
            btnCloseModal.addEventListener('click', () => this.closeModal());
        }

        const modalOverlay = document.getElementById('modalOverlay');
        if (modalOverlay) {
            modalOverlay.addEventListener('click', () => this.closeModal());
        }

        // Tabs
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', () => this.switchTab(btn.dataset.tab));
        });

        // Form Submit
        const evolutionForm = document.getElementById('evolutionForm');
        if (evolutionForm) {
            evolutionForm.addEventListener('submit', (e) => this.handleEvolutionSubmit(e));
        }
    }

    changePage(pageName) {
        // Update active nav
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        event.target.closest('.nav-item').classList.add('active');

        // Hide all pages
        document.querySelectorAll('.page').forEach(page => {
            page.classList.add('hidden');
        });

        // Show selected page
        const pageMap = {
            'pacientes': 'pagePacientes',
            'anamnese': 'pageAnamnese',
            'procedimentos': 'pageProcedimentos',
            'relatorios': 'pageRelatorios',
            'configuracoes': 'pageConfiguracoes'
        };

        const pageId = pageMap[pageName];
        if (pageId) {
            document.getElementById(pageId).classList.remove('hidden');
        }

        feather.replace();
    }

    async loadPacientes() {
        try {
            const filterStatus = document.getElementById('filterStatus')?.value;
            
            let pacientes;
            if (filterStatus === 'true') {
                pacientes = await api.listarPacientesAtivos();
            } else if (filterStatus === 'false') {
                pacientes = await api.listarPacientesInativos();
            } else {
                pacientes = await api.listarTodosPacientes();
            }
            
            this.allPacientes = pacientes;
            this.renderPacientesTable(pacientes);
        } catch (error) {
            console.error('Erro ao carregar pacientes:', error);
            this.showError('Erro ao carregar pacientes');
        }
    }

    renderPacientesTable(pacientes) {
        const tbody = document.getElementById('pacientesTableBody');
        if (!tbody) return;

        if (pacientes.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center" style="padding: 3rem;">
                        <p style="color: #666;">Nenhum paciente encontrado</p>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = pacientes.map(p => `
            <tr>
                <td>${p.idPaciente}</td>
                <td>${p.nomePessoa || '-'}</td>
                <td>${p.rgPaciente || '-'}</td>
                <td>${p.estdoRgPac || '-'}</td>
                <td>
                    <span class="status-badge ${p.statusPac ? 'ativo' : 'inativo'}">
                        ${p.statusPac ? 'Ativo' : 'Inativo'}
                    </span>
                </td>
                <td class="table-actions">
                    <button class="btn-icon" onclick="app.openAtendimento(${p.idPaciente})" title="Atendimento">
                        <i data-feather="clipboard"></i>
                    </button>
                    <button class="btn-icon" onclick="app.viewInfo(${p.idPaciente})" title="Informações">
                        <i data-feather="info"></i>
                    </button>
                </td>
            </tr>
        `).join('');

        feather.replace();
    }

    filterPacientes(term) {
        const lowerTerm = term.toLowerCase();
        const filtered = this.allPacientes.filter(p => 
            p.nomePessoa?.toLowerCase().includes(lowerTerm) ||
            p.rgPaciente?.toLowerCase().includes(lowerTerm) ||
            p.idPaciente.toString().includes(lowerTerm) ||
            p.estdoRgPac?.toLowerCase().includes(lowerTerm)
        );
        this.renderPacientesTable(filtered);
    }

    async openAtendimento(idPaciente) {
        try {
            this.showLoading(true);
            
            // Buscar dados do paciente
            const paciente = this.allPacientes.find(p => p.idPaciente === idPaciente) || 
                             await api.buscarPacientePorId(idPaciente);
            
            this.currentPatient = paciente;

            // Update modal title
            document.getElementById('modalTitle').textContent = 
                `Atendimento - ${paciente.nomePessoa || paciente.rgPaciente} (ID: ${paciente.idPaciente})`;

            // Show modal
            document.getElementById('modalAtendimento').classList.remove('hidden');
            this.switchTab('nova-evolucao');

            // Carregar detalhes completos do paciente usando o novo serviço
            await pacienteDetalhesManager.carregarDetalhesCompletos(idPaciente);

            feather.replace();
        } catch (error) {
            console.error('Erro ao abrir atendimento:', error);
            this.showError('Erro ao carregar dados do paciente');
        } finally {
            this.showLoading(false);
        }
    }

    switchTab(tabName) {
        // Update buttons
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        const activeBtn = document.querySelector(`[data-tab="${tabName}"]`);
        if (activeBtn) {
            activeBtn.classList.add('active');
        }

        // Update content
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });

        const contentMap = {
            'nova-evolucao': 'tabNovaEvolucao',
            'historico': 'tabHistorico',
            'info': 'tabInfo'
        };

        const contentId = contentMap[tabName];
        if (contentId) {
            document.getElementById(contentId).classList.add('active');
        }
    }

    updateHistoricoTab() {
        const historicoList = document.getElementById('historicoList');
        if (!historicoList) return;

        if (this.currentAtendimentos.length === 0) {
            historicoList.innerHTML = '<p style="color: #666; text-align: center; padding: 2rem;">Nenhum atendimento registrado</p>';
            return;
        }

        historicoList.innerHTML = this.currentAtendimentos.map(atd => `
            <div class="history-item">
                <div class="h-date">${new Date(atd.dataAtendi).toLocaleDateString('pt-BR')}</div>
                <div class="h-summary">
                    <p><strong>S:</strong> ${atd.subjetivo || '-'}</p>
                    <p><strong>O:</strong> ${atd.objetivo || '-'}</p>
                    <p><strong>A:</strong> ${atd.avaliacao || '-'}</p>
                    <p><strong>P:</strong> ${atd.plano || '-'}</p>
                </div>
            </div>
        `).join('');
    }

    updateInfoTab() {
        const pacienteInfo = document.getElementById('pacienteInfo');
        if (!pacienteInfo || !this.currentPatient) return;

        pacienteInfo.innerHTML = `
            <div class="info-item">
                <div class="info-label">ID</div>
                <div class="info-value">${this.currentPatient.idPaciente}</div>
            </div>
            <div class="info-item">
                <div class="info-label">RG</div>
                <div class="info-value">${this.currentPatient.rgPaciente || '-'}</div>
            </div>
            <div class="info-item">
                <div class="info-label">Estado RG</div>
                <div class="info-value">${this.currentPatient.estdoRgPac || '-'}</div>
            </div>
            <div class="info-item">
                <div class="info-label">Status</div>
                <div class="info-value">
                    <span class="status-badge ${this.currentPatient.statusPac ? 'ativo' : 'inativo'}">
                        ${this.currentPatient.statusPac ? 'Ativo' : 'Inativo'}
                    </span>
                </div>
            </div>
            <div class="info-item">
                <div class="info-label">Total de Atendimentos</div>
                <div class="info-value">${this.currentAtendimentos.length}</div>
            </div>
        `;
    }

    async handleEvolutionSubmit(e) {
        e.preventDefault();

        if (!this.currentPatient) {
            this.showError('Nenhum paciente selecionado');
            return;
        }

        const data = {
            idPaciente: this.currentPatient.idPaciente,
            idProfissio: parseInt(document.getElementById('inputProf').value),
            codProced: document.getElementById('inputProc').value,
            dataAtendimento: document.getElementById('inputDate').value,
            subjetivo: document.getElementById('inputS').value,
            objetivo: document.getElementById('inputO').value,
            avaliacao: document.getElementById('inputA').value,
            plano: document.getElementById('inputP').value
        };

        try {
            this.showLoading(true);
            await api.criarAtendimentoSOAP(data);
            
            alert('Evolução salva com sucesso!');
            
            // Reload data
            await this.openAtendimento(this.currentPatient.idPaciente);
            
            // Clear form
            e.target.reset();
            document.getElementById('inputDate').valueAsDate = new Date();
        } catch (error) {
            console.error('Erro ao salvar evolução:', error);
            this.showError('Erro ao salvar evolução: ' + (error.message || 'Erro desconhecido'));
        } finally {
            this.showLoading(false);
        }
    }

    closeModal() {
        document.getElementById('modalAtendimento').classList.add('hidden');
        this.currentPatient = null;
    }

    viewInfo(idPaciente) {
        this.openAtendimento(idPaciente);
        setTimeout(() => this.switchTab('info'), 300);
    }

    showLoading(show) {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.classList.toggle('hidden', !show);
        }
    }

    showError(message) {
        alert(message); // Pode ser substituído por toast/notification mais sofisticado
    }
}

// Initialize app
const app = new FisioclinApp();

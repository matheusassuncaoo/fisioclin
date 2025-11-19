/**
 * Evolution Manager - Gerenciamento Avançado de Evoluções Fisioterapêuticas
 * Foco: Velocidade, Análise Visual e Comparação Inteligente
 */

class EvolutionManager {
    constructor() {
        this.currentPatient = null;
        this.currentPessoaFis = null;
        this.currentAtendimentos = [];
        this.editingAtendimento = null;
        this.chart = null;
        this.allPacientes = [];
        this.init();
    }

    async init() {
        await this.loadInitialData();
        this.setupEventListeners();
        this.setupSOAPTabs();
        this.setupQuickTags();
    }

    async loadInitialData() {
        try {
            this.allPacientes = await api.listarPacientesAtivos();
            this.updateQuickStats();
        } catch (error) {
            console.error('Erro ao carregar dados iniciais:', error);
        }
    }

    setupEventListeners() {
        // Busca inteligente
        const searchInput = document.getElementById('searchInput');
        searchInput.addEventListener('input', () => this.handleSearch());
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.selectFirstResult();
        });

        document.getElementById('searchBtn').addEventListener('click', () => this.selectFirstResult());

        // Formulário SOAP
        document.getElementById('evolutionFormElement').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveEvolution();
        });

        document.getElementById('cancelBtn').addEventListener('click', () => this.cancelEvolution());
        document.getElementById('copyLastBtn').addEventListener('click', () => this.copyLastSession());

        // Timeline e filtros
        document.getElementById('filterBtn').addEventListener('click', () => this.toggleFilterPanel());
        document.getElementById('compareBtn').addEventListener('click', () => this.toggleCompareMode());
        document.getElementById('applyFilterBtn').addEventListener('click', () => this.applyFilter());
        document.getElementById('clearFilterBtn').addEventListener('click', () => this.clearFilter());
        document.getElementById('runCompareBtn')?.addEventListener('click', () => this.runComparison());

        // Fechar dropdown ao clicar fora
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.search-input-wrapper')) {
                document.getElementById('searchResults').classList.add('hidden');
            }
        });
    }

    setupSOAPTabs() {
        const tabs = document.querySelectorAll('.soap-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const targetTab = tab.dataset.tab;
                
                // Atualizar tabs
                tabs.forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                
                // Atualizar painéis
                document.querySelectorAll('.soap-panel').forEach(p => p.classList.remove('active'));
                document.querySelector(`[data-panel="${targetTab}"]`).classList.add('active');
            });
        });
    }

    setupQuickTags() {
        document.querySelectorAll('.tag-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                const tag = chip.dataset.tag;
                const panel = chip.closest('.soap-panel');
                const textarea = panel.querySelector('textarea');
                
                chip.classList.toggle('selected');
                
                // Adicionar/remover tag do texto
                if (chip.classList.contains('selected')) {
                    const currentText = textarea.value;
                    textarea.value = currentText ? `${currentText}; ${tag}` : tag;
                } else {
                    textarea.value = textarea.value.replace(new RegExp(`(; )?${tag}`, 'g'), '').trim();
                }
            });
        });
    }

    // ========== BUSCA INTELIGENTE ==========
    handleSearch() {
        const query = document.getElementById('searchInput').value.trim().toLowerCase();
        const resultsDiv = document.getElementById('searchResults');

        if (query.length < 2) {
            resultsDiv.classList.add('hidden');
            return;
        }

        // Buscar por nome, CPF ou ID
        const results = this.allPacientes.filter(p => {
            const idMatch = p.idPaciente.toString().includes(query);
            const rgMatch = p.rgPaciente.toLowerCase().includes(query);
            return idMatch || rgMatch;
        }).slice(0, 5);

        if (results.length === 0) {
            resultsDiv.innerHTML = '<div class="search-result-item">Nenhum paciente encontrado</div>';
            resultsDiv.classList.remove('hidden');
            return;
        }

        resultsDiv.innerHTML = results.map(p => `
            <div class="search-result-item" onclick="evolutionManager.loadPatientById(${p.idPaciente})">
                <strong>ID: ${p.idPaciente}</strong>
                <small>RG: ${p.rgPaciente} | ${p.estdoRgPac || 'N/A'}</small>
            </div>
        `).join('');

        resultsDiv.classList.remove('hidden');
    }

    selectFirstResult() {
        const firstItem = document.querySelector('.search-result-item');
        if (firstItem && firstItem.onclick) {
            firstItem.click();
        }
    }

    async loadPatientById(idPaciente) {
        document.getElementById('searchResults').classList.add('hidden');
        await this.loadPatientData(idPaciente);
    }

    // ========== CARREGAMENTO DE DADOS DO PACIENTE ==========
    async loadPatientData(patientId) {
        this.showLoading(true);
        
        try {
            const [paciente, atendimentos] = await Promise.all([
                api.buscarPacientePorId(patientId),
                api.buscarAtendimentosPorPaciente(patientId)
            ]);

            this.currentPatient = paciente;
            this.currentAtendimentos = atendimentos.sort((a, b) => 
                new Date(b.dataAtendi) - new Date(a.dataAtendi)
            );

            // Buscar dados da pessoa física
            await this.loadPessoaFis(paciente.idPessoaFis);

            this.displayPatientDashboard();
            this.displayTimeline();
            this.displayMetrics();
            this.displayChart();
            this.displayKeywords();
            
            this.showSections();
            this.showMessage('Paciente carregado com sucesso!', 'success');
        } catch (error) {
            this.showMessage('Erro ao carregar dados do paciente', 'error');
            console.error(error);
        } finally {
            this.showLoading(false);
        }
    }

    async loadPessoaFis(idPessoaFis) {
        // Como não temos endpoint direto, vamos usar a view VW_PESSOAFIS se existir
        // Por enquanto, guardar apenas o ID
        this.currentPessoaFis = { idPessoaFis };
    }

    // ========== DASHBOARD VISUAL ==========
    displayPatientDashboard() {
        const header = document.getElementById('patientHeader');
        const p = this.currentPatient;

        header.innerHTML = `
            <div class="patient-name">Paciente ID: ${p.idPaciente}</div>
            <div class="patient-info-grid">
                <div class="patient-info-item">
                    <i data-feather="credit-card"></i>
                    <span>RG: ${p.rgPaciente}</span>
                </div>
                <div class="patient-info-item">
                    <i data-feather="map-pin"></i>
                    <span>UF: ${p.estdoRgPac || 'N/A'}</span>
                </div>
                <div class="patient-info-item">
                    <i data-feather="${p.statusPac ? 'check-circle' : 'x-circle'}"></i>
                    <span>${p.statusPac ? 'Ativo' : 'Inativo'}</span>
                </div>
                <div class="patient-info-item">
                    <i data-feather="user"></i>
                    <span>ID Pessoa: ${p.idPessoaFis}</span>
                </div>
            </div>
        `;

        feather.replace();
    }

    displayMetrics() {
        const atends = this.currentAtendimentos;
        
        if (atends.length === 0) {
            document.getElementById('totalSessoes').textContent = '0';
            document.getElementById('ultimaSessao').textContent = '-';
            document.getElementById('diasTratamento').textContent = '0';
            document.getElementById('frequenciaSemanal').textContent = '-';
            return;
        }

        // Total de sessões
        document.getElementById('totalSessoes').textContent = atends.length;

        // Última sessão
        const ultimaData = new Date(atends[0].dataAtendi);
        const diasDesdeUltima = Math.floor((new Date() - ultimaData) / (1000 * 60 * 60 * 24));
        document.getElementById('ultimaSessao').textContent = 
            diasDesdeUltima === 0 ? 'Hoje' : 
            diasDesdeUltima === 1 ? 'Ontem' : 
            `${diasDesdeUltima} dias`;

        // Dias em tratamento
        const primeiraData = new Date(atends[atends.length - 1].dataAtendi);
        const diasTratamento = Math.floor((new Date() - primeiraData) / (1000 * 60 * 60 * 24));
        document.getElementById('diasTratamento').textContent = diasTratamento;

        // Frequência semanal
        const semanasCompletas = diasTratamento / 7;
        const frequencia = semanasCompletas > 0 ? (atends.length / semanasCompletas).toFixed(1) : '-';
        document.getElementById('frequenciaSemanal').textContent = frequencia + 'x';
    }

    displayChart() {
        const ctx = document.getElementById('evolutionChart');
        const atends = this.currentAtendimentos.slice().reverse().slice(-10); // Últimas 10 sessões

        if (this.chart) {
            this.chart.destroy();
        }

        const labels = atends.map(a => this.formatDate(a.dataAtendi));
        const dataLength = atends.map(a => a.descrAtendi ? a.descrAtendi.length : 0);

        this.chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Detalhamento da Evolução (caracteres)',
                    data: dataLength,
                    borderColor: 'rgb(79, 70, 229)',
                    backgroundColor: 'rgba(79, 70, 229, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => `${context.parsed.y} caracteres`
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 50
                        }
                    }
                }
            }
        });
    }

    displayKeywords() {
        const allText = this.currentAtendimentos
            .map(a => a.descrAtendi || '')
            .join(' ')
            .toLowerCase();

        // Palavras relevantes (removendo stopwords)
        const stopWords = ['o', 'a', 'de', 'da', 'do', 'e', 'que', 'em', 'para', 'com', 'no', 'na', 'os', 'as', 'dos', 'das', 'ao', 'à'];
        
        const words = allText
            .replace(/[.,;:!?]/g, '')
            .split(/\s+/)
            .filter(w => w.length > 3 && !stopWords.includes(w));

        const wordCount = {};
        words.forEach(w => {
            wordCount[w] = (wordCount[w] || 0) + 1;
        });

        const topWords = Object.entries(wordCount)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 10);

        const tagsDiv = document.getElementById('keywordsTags');
        
        if (topWords.length === 0) {
            tagsDiv.innerHTML = '<em style="color: var(--gray-text);">Nenhuma evolução registrada ainda</em>';
            return;
        }

        tagsDiv.innerHTML = topWords.map(([word, count]) => `
            <span class="keyword-tag">
                ${word}
                <span class="count">${count}</span>
            </span>
        `).join('');
    }

    displayTimeline() {
        const timeline = document.getElementById('timeline');
        const atends = this.currentAtendimentos;
        
        if (atends.length === 0) {
            timeline.innerHTML = '<div class="empty-state">Nenhum atendimento registrado</div>';
            return;
        }

        timeline.innerHTML = atends.map(a => `
            <div class="timeline-item" data-id="${a.idAtendiFisio}">
                <div class="timeline-item-header">
                    <div class="timeline-date">${this.formatDate(a.dataAtendi)}</div>
                    <div class="timeline-actions">
                        <button class="btn btn-small btn-primary" onclick="evolutionManager.editEvolution(${a.idAtendiFisio})">
                            <i data-feather="edit-2"></i>
                            Editar
                        </button>
                        <button class="btn btn-small btn-danger" onclick="evolutionManager.deleteEvolution(${a.idAtendiFisio})">
                            <i data-feather="trash-2"></i>
                            Excluir
                        </button>
                    </div>
                </div>
                <div class="timeline-description">
                    ${a.descrAtendi || '<em>Sem descrição</em>'}
                </div>
                <div class="timeline-meta">
                    <span><strong>ID:</strong> ${a.idAtendiFisio}</span>
                    <span><strong>Profissional:</strong> ${a.idProfissio}</span>
                    <span><strong>Procedimento:</strong> ${a.idProced}</span>
                </div>
            </div>
        `).join('');
        
        feather.replace();
    }

    // ========== SALVAR EVOLUÇÃO (SOAP) ==========
    async saveEvolution() {
        if (!this.currentPatient) {
            this.showMessage('Nenhum paciente selecionado', 'error');
            return;
        }

        // Coletar texto SOAP
        const subjetivo = document.getElementById('textoSubjetivo').value;
        const objetivo = document.getElementById('textoObjetivo').value;
        const avaliacao = document.getElementById('textoAvaliacao').value;
        const plano = document.getElementById('textoPlano').value;

        // Montar descrição completa
        let descricao = '';
        if (subjetivo) descricao += `S: ${subjetivo}\n`;
        if (objetivo) descricao += `O: ${objetivo}\n`;
        if (avaliacao) descricao += `A: ${avaliacao}\n`;
        if (plano) descricao += `P: ${plano}`;

        const formData = {
            idPaciente: this.currentPatient.idPaciente,
            idProfissio: parseInt(document.getElementById('idProfissio').value),
            idProced: parseInt(document.getElementById('idProced').value),
            dataAtendi: document.getElementById('dataAtendi').value,
            descrAtendi: descricao.trim().substring(0, 250)
        };

        if (!formData.idProfissio || !formData.idProced || !formData.dataAtendi) {
            this.showMessage('Preencha todos os campos obrigatórios', 'error');
            return;
        }

        this.showLoading(true);

        try {
            if (this.editingAtendimento) {
                await api.atualizarAtendimento(this.editingAtendimento, formData);
                this.showMessage('Evolução atualizada!', 'success');
            } else {
                await api.criarAtendimento(formData);
                this.showMessage('Evolução registrada!', 'success');
            }

            await this.loadPatientData(this.currentPatient.idPaciente);
            this.resetForm();
        } catch (error) {
            this.showMessage('Erro: ' + error.message, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async editEvolution(id) {
        const atendimento = this.currentAtendimentos.find(a => a.idAtendiFisio === id);
        if (!atendimento) return;

        this.editingAtendimento = id;
        
        document.getElementById('idProfissio').value = atendimento.idProfissio;
        document.getElementById('idProced').value = atendimento.idProced;
        document.getElementById('dataAtendi').value = atendimento.dataAtendi;

        // Parsear SOAP
        const descr = atendimento.descrAtendi || '';
        const soapMatch = {
            S: descr.match(/S:\s*([^\n]+)/),
            O: descr.match(/O:\s*([^\n]+)/),
            A: descr.match(/A:\s*([^\n]+)/),
            P: descr.match(/P:\s*([^\n]+)/)
        };

        document.getElementById('textoSubjetivo').value = soapMatch.S ? soapMatch.S[1] : descr;
        document.getElementById('textoObjetivo').value = soapMatch.O ? soapMatch.O[1] : '';
        document.getElementById('textoAvaliacao').value = soapMatch.A ? soapMatch.A[1] : '';
        document.getElementById('textoPlano').value = soapMatch.P ? soapMatch.P[1] : '';

        document.getElementById('evolutionForm').scrollIntoView({ behavior: 'smooth' });
        this.showMessage('Editando evolução', 'info');
    }

    async deleteEvolution(id) {
        if (!confirm('Excluir esta evolução?')) return;

        this.showLoading(true);
        try {
            await api.deletarAtendimento(id);
            this.showMessage('Evolução excluída!', 'success');
            await this.loadPatientData(this.currentPatient.idPaciente);
        } catch (error) {
            this.showMessage('Erro: ' + error.message, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    copyLastSession() {
        if (this.currentAtendimentos.length === 0) return;

        const last = this.currentAtendimentos[0];
        const descr = last.descrAtendi || '';
        
        const soapMatch = {
            S: descr.match(/S:\s*([^\n]+)/),
            O: descr.match(/O:\s*([^\n]+)/),
            A: descr.match(/A:\s*([^\n]+)/),
            P: descr.match(/P:\s*([^\n]+)/)
        };

        document.getElementById('textoSubjetivo').value = soapMatch.S ? soapMatch.S[1] : descr;
        document.getElementById('textoObjetivo').value = soapMatch.O ? soapMatch.O[1] : '';
        document.getElementById('textoAvaliacao').value = soapMatch.A ? soapMatch.A[1] : '';
        document.getElementById('textoPlano').value = soapMatch.P ? soapMatch.P[1] : '';

        this.showMessage('Última sessão copiada!', 'info');
    }

    cancelEvolution() {
        this.resetForm();
        this.showMessage('Operação cancelada', 'info');
    }

    resetForm() {
        document.getElementById('evolutionFormElement').reset();
        document.querySelectorAll('.tag-chip').forEach(c => c.classList.remove('selected'));
        this.editingAtendimento = null;
        document.getElementById('dataAtendi').value = new Date().toISOString().split('T')[0];
    }

    // ========== FILTROS E COMPARAÇÃO ==========
    toggleFilterPanel() {
        document.getElementById('filterPanel').classList.toggle('hidden');
    }

    toggleCompareMode() {
        const panel = document.getElementById('comparePanel');
        panel.classList.toggle('hidden');

        if (!panel.classList.contains('hidden')) {
            this.populateCompareSelects();
        }
    }

    populateCompareSelects() {
        const selectA = document.getElementById('compareA');
        const selectB = document.getElementById('compareB');

        const options = this.currentAtendimentos.map(a => `
            <option value="${a.idAtendiFisio}">
                ${this.formatDate(a.dataAtendi)} - ${a.descrAtendi?.substring(0, 30) || 'Sem descrição'}...
            </option>
        `).join('');

        selectA.innerHTML = options;
        selectB.innerHTML = options;

        if (this.currentAtendimentos.length > 1) {
            selectB.selectedIndex = 1;
        }
    }

    runComparison() {
        const idA = parseInt(document.getElementById('compareA').value);
        const idB = parseInt(document.getElementById('compareB').value);

        const sessaoA = this.currentAtendimentos.find(a => a.idAtendiFisio === idA);
        const sessaoB = this.currentAtendimentos.find(a => a.idAtendiFisio === idB);

        const resultDiv = document.getElementById('compareResult');
        
        resultDiv.innerHTML = `
            <h4>Comparação de Sessões</h4>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 16px;">
                <div>
                    <strong>${this.formatDate(sessaoA.dataAtendi)}</strong>
                    <p style="margin-top: 8px;">${sessaoA.descrAtendi || 'Sem descrição'}</p>
                </div>
                <div>
                    <strong>${this.formatDate(sessaoB.dataAtendi)}</strong>
                    <p style="margin-top: 8px;">${sessaoB.descrAtendi || 'Sem descrição'}</p>
                </div>
            </div>
        `;
    }

    async applyFilter() {
        const dataInicio = document.getElementById('dataInicio').value;
        const dataFim = document.getElementById('dataFim').value;

        if (!dataInicio || !dataFim) {
            this.showMessage('Selecione data início e fim', 'error');
            return;
        }

        this.showLoading(true);
        try {
            const filtered = await api.buscarEvolucaoPaciente(
                this.currentPatient.idPaciente,
                dataInicio,
                dataFim
            );
            
            this.currentAtendimentos = filtered.sort((a, b) => 
                new Date(b.dataAtendi) - new Date(a.dataAtendi)
            );
            
            this.displayTimeline();
            this.displayChart();
            this.showMessage(`${filtered.length} atendimento(s) no período`, 'info');
        } catch (error) {
            this.showMessage('Erro ao filtrar: ' + error.message, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async clearFilter() {
        document.getElementById('dataInicio').value = '';
        document.getElementById('dataFim').value = '';
        await this.loadPatientData(this.currentPatient.idPaciente);
    }

    // ========== UTILIDADES ==========
    showSections() {
        document.getElementById('patientDashboard').classList.add('active');
        document.getElementById('evolutionForm').classList.add('active');
        document.getElementById('timelineSection').classList.add('active');
    }

    updateQuickStats() {
        document.getElementById('totalPacientes').textContent = this.allPacientes.length;
        document.getElementById('atendimentosHoje').textContent = '0'; // Implementar se necessário
    }

    showMessage(message, type = 'info') {
        const messageDiv = document.getElementById('message');
        messageDiv.className = `message message-${type} active`;
        messageDiv.textContent = message;

        setTimeout(() => {
            messageDiv.classList.remove('active');
        }, 5000);
    }

    showLoading(show) {
        const loading = document.getElementById('loading');
        loading.classList.toggle('active', show);
    }

    formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString + 'T00:00:00');
        return date.toLocaleDateString('pt-BR');
    }
}

// Inicializar
let evolutionManager;
document.addEventListener('DOMContentLoaded', () => {
    evolutionManager = new EvolutionManager();
    document.getElementById('dataAtendi').value = new Date().toISOString().split('T')[0];
    feather.replace();
});

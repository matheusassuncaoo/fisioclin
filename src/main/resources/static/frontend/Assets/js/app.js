/**
 * FisioClin - Sistema de Evolução de Pacientes
 * Aplicação SPA para Fisioterapia
 * 
 * @version 2.0.0
 * @author FisioClin Team
 */

class FisioApp {
    constructor() {
        // Estado da aplicação
        this.state = {
            telaAtual: 'selecao',
            pacienteAtual: null,
            pacientes: [],
            filtros: {
                busca: '',
                status: 'true',
                especialidade: '30' // Fisioterapia
            },
            // Preparado para autenticação futura
            usuario: {
                id: null,
                nome: 'Profissional',
                cargo: 'Fisioterapeuta',
                especialidade: '30'
            }
        };

        // Elementos do DOM
        this.elements = {};
        
        // Inicializar
        this.init();
    }

    async init() {
        console.log('🏥 FisioClin iniciando...');
        
        // Cachear elementos do DOM
        this.cacheElements();
        
        // Configurar event listeners
        this.setupEventListeners();
        
        // Configurar data de hoje no formulário
        this.setTodayDate();
        
        // Carregar dados iniciais em paralelo
        await Promise.all([
            this.carregarPacientes(),
            this.carregarProcedimentosFisioterapia()
        ]);
        
        // Configurar busca automática de nome do profissional
        this.configurarBuscaProfissional();
        
        // Atualizar ícones do Feather
        feather.replace();
        
        console.log('✅ FisioClin pronto!');
    }

    cacheElements() {
        this.elements = {
            // Header
            btnVoltar: document.getElementById('btnVoltar'),
            pageTitle: document.getElementById('pageTitle'),
            userName: document.getElementById('userName'),
            userRole: document.getElementById('userRole'),
            
            // Telas
            telaSelecao: document.getElementById('telaSelecao'),
            telaEvolucao: document.getElementById('telaEvolucao'),
            
            // Busca e filtros
            inputBusca: document.getElementById('inputBuscaPaciente'),
            btnLimparBusca: document.getElementById('btnLimparBusca'),
            filterStatus: document.getElementById('filterStatus'),
            filterEspecialidade: document.getElementById('filterEspecialidade'),
            
            // Grid de pacientes
            pacientesGrid: document.getElementById('pacientesGrid'),
            emptyState: document.getElementById('emptyStatePacientes'),
            
            // Dashboard
            totalPacientesAtivos: document.getElementById('totalPacientesAtivos'),
            atendimentosHoje: document.getElementById('atendimentosHoje'),
            anamnesesPendentes: document.getElementById('anamnesesPendentes'),
            
            // Dados do paciente
            pacienteIniciais: document.getElementById('pacienteIniciais'),
            pacienteNome: document.getElementById('pacienteNome'),
            pacienteId: document.getElementById('pacienteId'),
            pacienteCpf: document.getElementById('pacienteCpf'),
            pacienteRg: document.getElementById('pacienteRg'),
            pacienteStatus: document.getElementById('pacienteStatus'),
            
            // Stats rápidas
            statTotalSessoes: document.getElementById('statTotalSessoes'),
            statDiasTratamento: document.getElementById('statDiasTratamento'),
            statFrequencia: document.getElementById('statFrequencia'),
            statUltimoAtend: document.getElementById('statUltimoAtend'),
            
            // Formulário de Atendimento
            formEvolucao: document.getElementById('formEvolucao'),
            inputDataAtend: document.getElementById('inputDataAtend'),
            selectProcedimento: document.getElementById('selectProcedimento'),
            inputProfissional: document.getElementById('inputProfissional'),
            nomeProfissionalHint: document.getElementById('nomeProfissionalHint'),
            inputDescricao: document.getElementById('inputDescricao'),
            charCount: document.getElementById('charCount'),
            btnLimparForm: document.getElementById('btnLimparForm'),
            
            // Tabs
            tabBtns: document.querySelectorAll('.tab-btn'),
            tabPanels: document.querySelectorAll('.tab-panel'),
            
            // Histórico
            historicoTimeline: document.getElementById('historicoTimeline'),
            
            // Exercícios
            exerciciosList: document.getElementById('exerciciosList'),
            
            // Loading
            loadingOverlay: document.getElementById('loadingOverlay'),
            
            // Modal Detalhes
            modalDetalhes: document.getElementById('modalDetalhes'),
            modalData: document.getElementById('modalData'),
            modalProcedimento: document.getElementById('modalProcedimento'),
            modalProfissional: document.getElementById('modalProfissional'),
            modalDescricao: document.getElementById('modalDescricao'),
            btnCloseModal: document.getElementById('btnCloseModal'),
            btnFecharModal: document.getElementById('btnFecharModal')
        };
    }

    setupEventListeners() {
        // Botão Voltar
        this.elements.btnVoltar?.addEventListener('click', () => this.voltarParaSelecao());
        
        // Busca
        this.elements.inputBusca?.addEventListener('input', (e) => this.handleBusca(e.target.value));
        this.elements.btnLimparBusca?.addEventListener('click', () => this.limparBusca());
        
        // Filtros
        this.elements.filterStatus?.addEventListener('change', () => this.aplicarFiltros());
        this.elements.filterEspecialidade?.addEventListener('change', () => this.aplicarFiltros());
        
        // Formulário
        this.elements.formEvolucao?.addEventListener('submit', (e) => this.handleSubmitEvolucao(e));
        this.elements.btnLimparForm?.addEventListener('click', () => this.limparFormulario());
        
        // Contador de caracteres
        this.elements.inputDescricao?.addEventListener('input', (e) => {
            const count = e.target.value.length;
            if (this.elements.charCount) {
                this.elements.charCount.textContent = count;
            }
        });
        
        // Tabs
        this.elements.tabBtns.forEach(btn => {
            btn.addEventListener('click', () => this.switchTab(btn.dataset.tab));
        });
        
        // Modal
        this.elements.btnCloseModal?.addEventListener('click', () => this.fecharModal());
        this.elements.btnFecharModal?.addEventListener('click', () => this.fecharModal());
        this.elements.modalDetalhes?.querySelector('.modal-overlay')?.addEventListener('click', () => this.fecharModal());
        
        // Fechar modal com ESC
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') this.fecharModal();
        });
    }

    setTodayDate() {
        if (this.elements.inputDataAtend) {
            this.elements.inputDataAtend.valueAsDate = new Date();
        }
    }

    // ==================== NAVEGAÇÃO ====================
    
    navegarPara(tela) {
        // Esconder todas as telas
        document.querySelectorAll('.tela').forEach(t => t.classList.remove('active'));
        
        // Mostrar tela desejada
        const telaElement = document.getElementById(`tela${tela.charAt(0).toUpperCase() + tela.slice(1)}`);
        if (telaElement) {
            telaElement.classList.add('active');
        }
        
        // Atualizar estado
        this.state.telaAtual = tela;
        
        // Atualizar header
        this.atualizarHeader();
        
        // Atualizar ícones
        feather.replace();
    }

    atualizarHeader() {
        const { telaAtual, pacienteAtual } = this.state;
        
        if (telaAtual === 'selecao') {
            this.elements.btnVoltar?.classList.add('hidden');
            this.elements.pageTitle.textContent = 'Selecionar Paciente';
        } else if (telaAtual === 'evolucao' && pacienteAtual) {
            this.elements.btnVoltar?.classList.remove('hidden');
            this.elements.pageTitle.textContent = `Evolução - ${pacienteAtual.nomePessoa || 'Paciente'}`;
        }
    }

    voltarParaSelecao() {
        this.state.pacienteAtual = null;
        this.navegarPara('selecao');
    }

    // ==================== PACIENTES ====================

    async carregarPacientes() {
        try {
            this.showLoading(true);
            
            const status = this.state.filtros.status;
            let pacientes;
            
            if (status === 'true') {
                pacientes = await api.listarPacientesAtivos();
            } else if (status === 'false') {
                pacientes = await api.listarPacientesInativos();
            } else {
                pacientes = await api.listarTodosPacientes();
            }
            
            this.state.pacientes = pacientes || [];
            this.renderizarPacientes();
            this.atualizarDashboard();
            
        } catch (error) {
            console.error('Erro ao carregar pacientes:', error);
            notifications.error('Erro ao carregar pacientes. Verifique a conexão com o servidor.');
            this.elements.pacientesGrid.innerHTML = `
                <div class="empty-state">
                    <i data-feather="alert-circle"></i>
                    <h3>Erro ao carregar</h3>
                    <p>${error.message}</p>
                </div>
            `;
        } finally {
            this.showLoading(false);
            feather.replace();
        }
    }

    renderizarPacientes() {
        const { pacientes, filtros } = this.state;
        
        // Aplicar busca
        let pacientesFiltrados = pacientes;
        if (filtros.busca) {
            const termo = filtros.busca.toLowerCase();
            pacientesFiltrados = pacientes.filter(p => 
                p.nomePessoa?.toLowerCase().includes(termo) ||
                p.rgPaciente?.toLowerCase().includes(termo) ||
                p.cpfPessoa?.includes(termo) ||
                p.idPaciente?.toString().includes(termo)
            );
        }
        
        // Renderizar
        if (pacientesFiltrados.length === 0) {
            this.elements.pacientesGrid.innerHTML = '';
            this.elements.emptyState?.classList.remove('hidden');
        } else {
            this.elements.emptyState?.classList.add('hidden');
            this.elements.pacientesGrid.innerHTML = pacientesFiltrados.map(p => this.criarCardPaciente(p)).join('');
            
            // Adicionar event listeners nos cards
            this.elements.pacientesGrid.querySelectorAll('.paciente-card').forEach(card => {
                card.addEventListener('click', () => {
                    const id = parseInt(card.dataset.id);
                    this.selecionarPaciente(id);
                });
            });
        }
        
        feather.replace();
    }

    criarCardPaciente(paciente) {
        const iniciais = this.getIniciais(paciente.nomePessoa);
        const statusClass = paciente.statusPac ? '' : 'inativo';
        
        return `
            <div class="paciente-card" data-id="${paciente.idPaciente}">
                <div class="paciente-card-avatar">${iniciais}</div>
                <div class="paciente-card-info">
                    <div class="paciente-card-nome">${paciente.nomePessoa || 'Sem nome'}</div>
                    <div class="paciente-card-meta">
                        <span>ID: ${paciente.idPaciente}</span>
                        <span>RG: ${paciente.rgPaciente || '--'}</span>
                    </div>
                </div>
                <div class="paciente-card-arrow">
                    <i data-feather="chevron-right"></i>
                </div>
            </div>
        `;
    }

    getIniciais(nome) {
        if (!nome) return '??';
        const partes = nome.split(' ').filter(p => p.length > 0);
        if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
        return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
    }

    async selecionarPaciente(idPaciente) {
        try {
            this.showLoading(true);
            
            // Buscar detalhes completos do paciente
            const detalhes = await api.buscarDetalhesCompletos(idPaciente);
            
            if (!detalhes) {
                throw new Error('Paciente não encontrado');
            }
            
            this.state.pacienteAtual = detalhes;
            
            // Preencher dados na tela
            this.preencherDadosPaciente(detalhes);
            
            // Navegar para tela de evolução
            this.navegarPara('evolucao');
            
            // Carregar dados adicionais
            await this.carregarDadosEvolucao(idPaciente);
            
        } catch (error) {
            console.error('Erro ao selecionar paciente:', error);
            notifications.error('Erro ao carregar dados do paciente');
        } finally {
            this.showLoading(false);
            feather.replace();
        }
    }

    preencherDadosPaciente(detalhes) {
        // Avatar
        this.elements.pacienteIniciais.textContent = this.getIniciais(detalhes.nomePessoa);
        
        // Info básica
        this.elements.pacienteNome.textContent = detalhes.nomePessoa || 'Sem nome';
        this.elements.pacienteId.textContent = detalhes.idPaciente;
        this.elements.pacienteCpf.textContent = this.formatarCPF(detalhes.cpfPessoa) || '--';
        this.elements.pacienteRg.textContent = detalhes.rgPaciente || '--';
        
        // Status
        const statusEl = this.elements.pacienteStatus;
        if (detalhes.statusPac) {
            statusEl.textContent = 'Ativo';
            statusEl.classList.remove('inativo');
        } else {
            statusEl.textContent = 'Inativo';
            statusEl.classList.add('inativo');
        }
        
        // Estatísticas rápidas
        if (detalhes.estatisticas) {
            const stats = detalhes.estatisticas;
            this.elements.statTotalSessoes.textContent = stats.totalAtendimentos || 0;
            this.elements.statDiasTratamento.textContent = stats.diasEmTratamento || 0;
            this.elements.statFrequencia.textContent = stats.frequenciaSemanal 
                ? stats.frequenciaSemanal.toFixed(1) + 'x' 
                : '0x';
            this.elements.statUltimoAtend.textContent = stats.ultimoAtendimento 
                ? this.formatarData(stats.ultimoAtendimento) 
                : '--';
        }
    }

    async carregarDadosEvolucao(idPaciente) {
        const detalhes = this.state.pacienteAtual;
        
        // Carregar histórico
        this.renderizarHistorico(detalhes.ultimosAtendimentos);
        
        // Carregar exercícios realizados
        this.renderizarExercicios(detalhes.exerciciosAtivos);
    }

    // ==================== PROCEDIMENTOS ====================

    async carregarProcedimentosFisioterapia() {
        const select = this.elements.selectProcedimento;
        if (!select) return;

        try {
            const procedimentos = await api.listarProcedimentosFisioterapia();
            
            // Limpar opções existentes (exceto a primeira)
            while (select.options.length > 1) {
                select.remove(1);
            }
            
            if (procedimentos && procedimentos.length > 0) {
                // Ordenar por código numérico (FT001, FT002, FT003...)
                procedimentos.sort((a, b) => {
                    // Extrair apenas os números do código (ex: "FT001" -> 1)
                    const numA = parseInt((a.codProced || '').replace(/\D/g, '')) || 0;
                    const numB = parseInt((b.codProced || '').replace(/\D/g, '')) || 0;
                    return numA - numB;
                });
                
                procedimentos.forEach(proc => {
                    const option = document.createElement('option');
                    option.value = proc.codProced;
                    // Formatar: "Código - Descrição"
                    option.textContent = `${proc.codProced} - ${proc.descrProc}`;
                    option.title = proc.descrProc; // Tooltip com descrição completa
                    select.appendChild(option);
                });
                
                console.log(`✅ ${procedimentos.length} procedimentos de fisioterapia carregados`);
            } else {
                // Fallback: manter opções estáticas se não houver dados
                console.warn('⚠️ Nenhum procedimento de fisioterapia encontrado na API, usando fallback');
                this.carregarProcedimentosFallback();
            }
        } catch (error) {
            console.error('Erro ao carregar procedimentos:', error);
            // Manter opções estáticas como fallback
            this.carregarProcedimentosFallback();
        }
    }

    carregarProcedimentosFallback() {
        const select = this.elements.selectProcedimento;
        if (!select) return;
        
        // Limpar e adicionar opções padrão de fisioterapia
        while (select.options.length > 1) {
            select.remove(1);
        }
        
        const procedimentosPadrao = [
            { cod: '30010010', desc: 'Avaliação Fisioterapêutica' },
            { cod: '30010020', desc: 'Sessão de Fisioterapia' },
            { cod: '30010030', desc: 'Eletroterapia' },
            { cod: '30010040', desc: 'Cinesioterapia' },
            { cod: '30010050', desc: 'Terapia Manual' },
            { cod: '30010060', desc: 'Hidroterapia' },
            { cod: '30010070', desc: 'RPG - Reeducação Postural' },
            { cod: '30010080', desc: 'Pilates Terapêutico' }
        ];
        
        procedimentosPadrao.forEach(proc => {
            const option = document.createElement('option');
            option.value = proc.cod;
            option.textContent = `${proc.cod} - ${proc.desc}`;
            select.appendChild(option);
        });
    }

    // ==================== PROFISSIONAIS ====================

    /**
     * Configura busca automática do nome do profissional quando o ID é digitado
     */
    configurarBuscaProfissional() {
        const input = this.elements.inputProfissional;
        const hint = this.elements.nomeProfissionalHint;
        
        if (!input) return;
        
        // Cache de nomes de profissionais
        this.profissionaisCache = this.profissionaisCache || {};
        
        // Buscar nome quando o usuário digitar o ID
        let buscaTimeout;
        input.addEventListener('input', (e) => {
            const idProfissio = e.target.value.trim();
            
            // Limpar hint anterior
            if (hint) {
                hint.style.display = 'none';
                hint.textContent = '';
            }
            
            // Limpar timeout anterior
            clearTimeout(buscaTimeout);
            
            // Se tiver ID válido, buscar nome após 500ms de inatividade
            if (idProfissio && !isNaN(idProfissio) && parseInt(idProfissio) > 0) {
                buscaTimeout = setTimeout(async () => {
                    try {
                        // Verificar cache primeiro
                        if (this.profissionaisCache[idProfissio]) {
                            if (hint) {
                                hint.textContent = `✓ ${this.profissionaisCache[idProfissio]}`;
                                hint.style.display = 'block';
                            }
                            return;
                        }
                        
                        // Buscar nome via API
                        const nome = await api.buscarNomeProfissional(parseInt(idProfissio));
                        if (nome) {
                            this.profissionaisCache[idProfissio] = nome;
                            if (hint) {
                                hint.textContent = `✓ ${nome}`;
                                hint.style.display = 'block';
                            }
                        }
                    } catch (error) {
                        // Se não encontrar, não mostra nada (silencioso)
                        console.log('Profissional não encontrado ou erro ao buscar nome');
                    }
                }, 500);
            }
        });
    }
    
    // Buscar nome do profissional (com cache)
    getNomeProfissional(idProfissio) {
        if (!idProfissio) return '--';
        if (this.profissionaisCache && this.profissionaisCache[idProfissio]) {
            return this.profissionaisCache[idProfissio];
        }
        return `Profissional #${idProfissio}`;
    }

    // ==================== BUSCA E FILTROS ====================

    handleBusca(termo) {
        this.state.filtros.busca = termo;
        
        // Mostrar/esconder botão limpar
        if (termo.length > 0) {
            this.elements.btnLimparBusca?.classList.remove('hidden');
        } else {
            this.elements.btnLimparBusca?.classList.add('hidden');
        }
        
        // Aplicar filtro com debounce
        clearTimeout(this.buscaTimeout);
        this.buscaTimeout = setTimeout(() => {
            this.renderizarPacientes();
        }, 300);
    }

    limparBusca() {
        this.elements.inputBusca.value = '';
        this.state.filtros.busca = '';
        this.elements.btnLimparBusca?.classList.add('hidden');
        this.renderizarPacientes();
    }

    aplicarFiltros() {
        this.state.filtros.status = this.elements.filterStatus.value;
        this.state.filtros.especialidade = this.elements.filterEspecialidade.value;
        this.carregarPacientes();
    }

    // ==================== TABS ====================

    switchTab(tabName) {
        // Atualizar botões
        this.elements.tabBtns.forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === tabName);
        });
        
        // Atualizar panels
        this.elements.tabPanels.forEach(panel => {
            panel.classList.toggle('active', panel.id === `tab${tabName.charAt(0).toUpperCase() + tabName.slice(1)}`);
        });
        
        feather.replace();
    }

    // ==================== FORMULÁRIO DE ATENDIMENTO ====================

    async handleSubmitEvolucao(e) {
        e.preventDefault();
        
        const paciente = this.state.pacienteAtual;
        if (!paciente) {
            notifications.error('Nenhum paciente selecionado');
            return;
        }
        
        // Validações
        const data = this.elements.inputDataAtend.value;
        const procedimento = this.elements.selectProcedimento.value;
        const profissional = this.elements.inputProfissional?.value;
        const descricao = this.elements.inputDescricao?.value.trim() || '';
        
        if (!data || !procedimento || !profissional) {
            notifications.warning('Preencha todos os campos obrigatórios');
            return;
        }
        
        // Montar payload simples
        const payload = {
            idPaciente: paciente.idPaciente,
            idProfissio: parseInt(profissional),
            codProced: procedimento,
            dataAtendimento: data,
            descricao: descricao
        };
        
        try {
            this.showLoading(true);
            
            await api.criarAtendimento(payload);
            
            notifications.success('Evolução salva com sucesso!', { title: 'Registrado' });
            
            // Limpar formulário
            this.limparFormulario();
            
            // Recarregar dados do paciente
            await this.selecionarPaciente(paciente.idPaciente);
            
        } catch (error) {
            console.error('Erro ao salvar evolução:', error);
            
            if (error.message.includes('inativo')) {
                notifications.error('Paciente inativo não pode receber evoluções');
            } else if (error.message.includes('Procedimento')) {
                notifications.error('Código de procedimento inválido');
            } else if (error.message.includes('Profissional')) {
                notifications.error('ID do profissional não encontrado');
            } else {
                notifications.error(error.message || 'Erro ao salvar evolução');
            }
        } finally {
            this.showLoading(false);
        }
    }

    limparFormulario() {
        this.elements.formEvolucao?.reset();
        this.setTodayDate();
        // Resetar contador de caracteres
        if (this.elements.charCount) {
            this.elements.charCount.textContent = '0';
        }
    }

    // ==================== RENDERIZAÇÃO ====================

    renderizarHistorico(atendimentos) {
        const container = this.elements.historicoTimeline;
        if (!container) return;
        
        // Guardar atendimentos para o modal
        this.atendimentosCache = atendimentos;
        
        if (!atendimentos || atendimentos.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i data-feather="clock"></i>
                    <h3>Sem histórico</h3>
                    <p>Nenhuma evolução registrada</p>
                </div>
            `;
            feather.replace();
            return;
        }
        
        container.innerHTML = atendimentos.map((atd, index) => {
            // Decodificar HTML entities para exibição correta
            const descricao = this.sanitizeForDisplay(atd.descricao || atd.descrAtendi || 'Sem descrição registrada');
            const procedimento = this.sanitizeForDisplay(atd.procedimento || atd.descrProc || atd.codProcedimento || 'Evolução');
            const profissional = this.sanitizeForDisplay(atd.nomeProfissional || this.getNomeProfissional(atd.idProfissio));
            
            return `
                <div class="timeline-item" data-index="${index}" onclick="app.abrirModalDetalhes(${index})">
                    <div class="timeline-date">${this.formatarData(atd.dataAtendimento || atd.dataAtendi)}</div>
                    <div class="timeline-content">
                        <h4>${procedimento}</h4>
                        <p class="timeline-descricao">${descricao}</p>
                        <small class="timeline-profissional">👤 ${profissional}</small>
                        <span class="ver-mais">Clique para ver mais →</span>
                    </div>
                </div>
            `;
        }).join('');
        
        feather.replace();
    }

    // ==================== MODAL ====================
    
    abrirModalDetalhes(index) {
        const atd = this.atendimentosCache?.[index];
        if (!atd) return;
        
        // Preencher dados (decodificando HTML entities)
        this.elements.modalData.textContent = this.formatarData(atd.dataAtendimento || atd.dataAtendi);
        
        // Procedimento: mostrar descrição ou código
        const procedimento = this.sanitizeForDisplay(atd.procedimento || atd.descrProc || atd.codProcedimento || '--');
        this.elements.modalProcedimento.textContent = procedimento;
        
        // Profissional: mostrar nome (não só o ID)
        const nomeProfissional = this.sanitizeForDisplay(atd.nomeProfissional || this.getNomeProfissional(atd.idProfissio));
        this.elements.modalProfissional.textContent = nomeProfissional;
        
        // Descrição
        this.elements.modalDescricao.textContent = this.sanitizeForDisplay(atd.descricao || atd.descrAtendi || 'Sem descrição registrada');
        
        // Mostrar modal
        this.elements.modalDetalhes?.classList.remove('hidden');
        feather.replace();
    }
    
    fecharModal() {
        this.elements.modalDetalhes?.classList.add('hidden');
    }

    renderizarExercicios(exercicios) {
        const container = this.elements.exerciciosList;
        if (!container) return;
        
        if (!exercicios || exercicios.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i data-feather="activity"></i>
                    <h3>Sem exercícios</h3>
                    <p>Nenhum exercício realizado registrado</p>
                    <p class="text-muted">O fluxo completo é: Anamnese → Prescrição → Realização</p>
                </div>
            `;
            feather.replace();
            return;
        }
        
        container.innerHTML = exercicios.map(ex => {
            // Decodificar HTML entities
            const nomeExerc = this.sanitizeForDisplay(ex.nomeExercicio || ex.descrExerc || 'Exercício');
            const orientacao = this.sanitizeForDisplay(ex.orientacao || '');
            
            return `
                <div class="exercicio-item">
                    <div class="exercicio-icon">
                        <i data-feather="check-circle"></i>
                    </div>
                    <div class="exercicio-info">
                        <div class="exercicio-nome">${nomeExerc}</div>
                        <div class="exercicio-meta">
                            ${ex.series ? ex.series + ' séries' : ''}
                            ${ex.repeticoes ? ' x ' + ex.repeticoes + ' repetições' : ''}
                            ${ex.ultimaRealizacao ? ' - ' + this.formatarData(ex.ultimaRealizacao) : ''}
                        </div>
                        ${orientacao ? `<div class="exercicio-orientacao">${orientacao}</div>` : ''}
                    </div>
                </div>
            `;
        }).join('');
        
        feather.replace();
    }

    async atualizarDashboard() {
        const { pacientes } = this.state;
        
        // Total de pacientes ativos (fallback local)
        const ativos = pacientes.filter(p => p.statusPac).length;
        this.elements.totalPacientesAtivos.textContent = ativos;
        
        // Buscar dados do dashboard via API
        try {
            const dashboard = await api.obterDashboard();
            
            if (dashboard) {
                // Total pacientes ativos (prioriza API)
                if (dashboard.totalPacientesAtivos !== undefined) {
                    this.elements.totalPacientesAtivos.textContent = dashboard.totalPacientesAtivos;
                }
                
                // Atendimentos hoje
                if (dashboard.atendimentosHoje !== undefined) {
                    this.elements.atendimentosHoje.textContent = dashboard.atendimentosHoje;
                } else {
                    this.elements.atendimentosHoje.textContent = '0';
                }
                
                // Anamneses pendentes
                if (dashboard.anamnesesPendentes !== undefined) {
                    this.elements.anamnesesPendentes.textContent = dashboard.anamnesesPendentes;
                } else {
                    this.elements.anamnesesPendentes.textContent = '0';
                }
            }
        } catch (error) {
            console.warn('Erro ao carregar dashboard:', error);
            // Manter contagem local de pacientes, zerar os outros
            this.elements.atendimentosHoje.textContent = '0';
            this.elements.anamnesesPendentes.textContent = '0';
        }
    }

    // ==================== UTILITÁRIOS ====================

    formatarCPF(cpf) {
        if (!cpf) return null;
        const numeros = cpf.replace(/\D/g, '');
        if (numeros.length !== 11) return cpf;
        return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }

    formatarData(data) {
        if (!data) return '--';
        try {
            // Criar data considerando timezone local
            const date = new Date(data + 'T00:00:00');
            return date.toLocaleDateString('pt-BR');
        } catch {
            return data;
        }
    }

    /**
     * Decodifica HTML entities para texto normal
     * Resolve problemas como &eacute; -> é, &amp; -> &
     */
    decodeHtmlEntities(text) {
        if (!text) return text;
        const textarea = document.createElement('textarea');
        textarea.innerHTML = text;
        return textarea.value;
    }

    /**
     * Sanitiza texto para exibição segura (decodifica HTML entities)
     */
    sanitizeForDisplay(text) {
        if (!text) return text;
        return this.decodeHtmlEntities(text);
    }

    showLoading(show) {
        if (show) {
            this.elements.loadingOverlay?.classList.remove('hidden');
        } else {
            this.elements.loadingOverlay?.classList.add('hidden');
        }
    }
}

// Exportar para uso global
window.FisioApp = FisioApp;


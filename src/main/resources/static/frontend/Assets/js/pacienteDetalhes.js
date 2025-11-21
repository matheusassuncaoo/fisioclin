/**
 * Gerenciamento de Detalhes do Paciente com Gráficos e Estatísticas
 */

class PacienteDetalhesManager {
    constructor() {
        this.chartInstance = null;
    }

    async carregarDetalhesCompletos(pacienteId) {
        try {
            const detalhes = await api.buscarDetalhesCompletos(pacienteId);
            console.log('Detalhes recebidos:', detalhes);
            
            // Preencher estatísticas
            this.preencherEstatisticas(detalhes.estatisticas);
            
            // Criar gráfico de evolução
            this.criarGraficoEvolucao(detalhes.evolucaoRecente);
            
            // Mostrar última anamnese
            this.mostrarUltimaAnamnese(detalhes.ultimaAnamnese);
            
            // Carregar histórico de atendimentos
            this.carregarHistorico(detalhes.ultimosAtendimentos);
            
            // Preencher informações básicas
            this.preencherInfoBasica(detalhes);
            
        } catch (error) {
            console.error('Erro ao carregar detalhes:', error);
            this.mostrarErro('Erro ao carregar informações do paciente');
        }
    }

    preencherEstatisticas(stats) {
        if (!stats) return;
        
        document.getElementById('statTotalAtend').textContent = stats.totalAtendimentos || '0';
        document.getElementById('statDiasTrat').textContent = stats.diasEmTratamento || '0';
        document.getElementById('statFreqSemanal').textContent = 
            stats.frequenciaSemanal ? stats.frequenciaSemanal.toFixed(1) + 'x' : '0x';
        document.getElementById('statTotalPront').textContent = stats.totalProntuarios || '0';
    }

    criarGraficoEvolucao(evolucaoData) {
        const canvas = document.getElementById('chartEvolucao');
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        
        // Destruir gráfico anterior se existir
        if (this.chartInstance) {
            this.chartInstance.destroy();
            this.chartInstance = null;
        }

        if (!evolucaoData || evolucaoData.length === 0) {
            canvas.parentElement.innerHTML = '<p style="text-align: center; color: #666; padding: 2rem;">Sem dados de evolução nos últimos 30 dias</p>';
            return;
        }

        const labels = evolucaoData.map(d => {
            const date = new Date(d.data);
            return date.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
        });
        
        const data = evolucaoData.map(d => d.quantidade);

        this.chartInstance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Atendimentos',
                    data: data,
                    borderColor: '#335e9e',
                    backgroundColor: 'rgba(51, 94, 158, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#335e9e',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: { size: 14, weight: 'bold' },
                        bodyFont: { size: 13 },
                        displayColors: false,
                        callbacks: {
                            label: function(context) {
                                return 'Atendimentos: ' + context.parsed.y;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1,
                            font: { size: 11 }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        ticks: {
                            font: { size: 11 },
                            maxRotation: 45,
                            minRotation: 45
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    mostrarUltimaAnamnese(anamnese) {
        const content = document.getElementById('ultimaAnamneseContent');
        if (!content) return;
        
        if (!anamnese) {
            content.innerHTML = '<p style="color: #666;">Nenhuma anamnese registrada</p>';
            return;
        }

        const dataFormatada = new Date(anamnese.dataAnamnese).toLocaleDateString('pt-BR');
        
        // Status da anamnese: APROVADO, REPROVADO, CANCELADO
        let statusBadge = '';
        const status = anamnese.statusAnamnese || '';
        
        if (status === 'APROVADO') {
            statusBadge = '<span style="background: #28a745; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">Aprovado</span>';
        } else if (status === 'REPROVADO') {
            statusBadge = '<span style="background: #dc3545; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">Reprovado</span>';
        } else if (status === 'CANCELADO') {
            statusBadge = '<span style="background: #6c757d; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">Cancelado</span>';
        } else {
            statusBadge = '<span style="background: #6c757d; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">' + status + '</span>';
        }

        content.innerHTML = `
            <p><strong>Data:</strong> ${dataFormatada}</p>
            <p><strong>Status:</strong> ${statusBadge}</p>
            ${anamnese.observacao ? `<p><strong>Observação:</strong> ${anamnese.observacao}</p>` : ''}
        `;
    }

    carregarHistorico(atendimentos) {
        const list = document.getElementById('historicoList');
        if (!list) return;
        
        if (!atendimentos || atendimentos.length === 0) {
            list.innerHTML = '<p style="text-align: center; color: #666; padding: 2rem;">Nenhum atendimento registrado</p>';
            return;
        }

        list.innerHTML = atendimentos.map(a => {
            const dataFormatada = new Date(a.dataAtendimento).toLocaleDateString('pt-BR');
            const badge = a.descricao 
                ? '<span style="background: #335e9e; color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem; margin-left: 0.5rem;">Com evolução</span>'
                : '';
            
            return `
                <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e5e5e5; margin-bottom: 0.75rem;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <strong style="color: #335e9e;">${dataFormatada}</strong>
                        ${badge}
                    </div>
                    ${a.descricao 
                        ? `<p style="margin: 0; color: #333; line-height: 1.5;">${a.descricao}</p>` 
                        : '<p style="margin: 0; color: #999; font-style: italic;">Sem descrição</p>'}
                </div>
            `;
        }).join('');
    }

    preencherInfoBasica(detalhes) {
        const infoGrid = document.querySelector('#tabInfo .info-grid');
        if (!infoGrid) return;
        
        if (!detalhes) {
            infoGrid.innerHTML = '<p>Erro ao carregar informações</p>';
            return;
        }

        infoGrid.innerHTML = `
            <div class="info-item">
                <div class="info-label">RG Paciente</div>
                <div class="info-value">${detalhes.rgPaciente || '-'}</div>
            </div>
            <div class="info-item">
                <div class="info-label">Status</div>
                <div class="info-value">${detalhes.statusPac ? 'Ativo' : 'Inativo'}</div>
            </div>
            <div class="info-item">
                <div class="info-label">Estado Órgão</div>
                <div class="info-value">${detalhes.estdoRgPac || '-'}</div>
            </div>
            <div class="info-item">
                <div class="info-label">ID Paciente</div>
                <div class="info-value">${detalhes.idPaciente || '-'}</div>
            </div>
        `;
    }

    mostrarErro(mensagem) {
        console.error(mensagem);
        // Você pode adicionar um toast/notification aqui se desejar
    }
}

// Criar instância global
const pacienteDetalhesManager = new PacienteDetalhesManager();

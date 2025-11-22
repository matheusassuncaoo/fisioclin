/**
 * Charts.js - Gráficos com Chart.js para FisioClin
 */

class ChartsManager {
    constructor() {
        this.charts = {};
    }

    /**
     * Cria gráfico de evolução do paciente
     */
    criarGraficoEvolucao(containerId, atendimentos) {
        const ctx = document.getElementById(containerId);
        if (!ctx) return null;

        // Destroi gráfico anterior se existir
        if (this.charts[containerId]) {
            this.charts[containerId].destroy();
        }

        // Prepara dados
        const datas = atendimentos.map(a => new Date(a.dataAtendi).toLocaleDateString('pt-BR'));
        const pressaoArterial = atendimentos.map(a => {
            if (a.preArtAtendi) {
                const match = a.preArtAtendi.match(/(\d+)\/(\d+)/);
                return match ? parseInt(match[1]) : null;
            }
            return null;
        });
        const frequenciaCardiaca = atendimentos.map(a => a.freCarAtendi || null);
        const temperatura = atendimentos.map(a => a.tempAtendi || null);

        this.charts[containerId] = new Chart(ctx, {
            type: 'line',
            data: {
                labels: datas,
                datasets: [
                    {
                        label: 'Pressão Arterial (Sistólica)',
                        data: pressaoArterial,
                        borderColor: '#ff6384',
                        backgroundColor: 'rgba(255, 99, 132, 0.1)',
                        tension: 0.3,
                        fill: true
                    },
                    {
                        label: 'Frequência Cardíaca (bpm)',
                        data: frequenciaCardiaca,
                        borderColor: '#36a2eb',
                        backgroundColor: 'rgba(54, 162, 235, 0.1)',
                        tension: 0.3,
                        fill: true
                    },
                    {
                        label: 'Temperatura (°C)',
                        data: temperatura,
                        borderColor: '#ffce56',
                        backgroundColor: 'rgba(255, 206, 86, 0.1)',
                        tension: 0.3,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Evolução dos Sinais Vitais',
                        font: { size: 16 }
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        title: {
                            display: true,
                            text: 'Valores'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Data do Atendimento'
                        }
                    }
                }
            }
        });

        return this.charts[containerId];
    }

    /**
     * Cria gráfico de distribuição de atendimentos por mês
     */
    criarGraficoAtendimentosMes(containerId, atendimentos) {
        const ctx = document.getElementById(containerId);
        if (!ctx) return null;

        if (this.charts[containerId]) {
            this.charts[containerId].destroy();
        }

        // Agrupa por mês
        const atendimentosPorMes = {};
        atendimentos.forEach(a => {
            const data = new Date(a.dataAtendi);
            const mesAno = `${data.getMonth() + 1}/${data.getFullYear()}`;
            atendimentosPorMes[mesAno] = (atendimentosPorMes[mesAno] || 0) + 1;
        });

        const meses = Object.keys(atendimentosPorMes).sort();
        const valores = meses.map(m => atendimentosPorMes[m]);

        this.charts[containerId] = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: meses,
                datasets: [{
                    label: 'Número de Atendimentos',
                    data: valores,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Atendimentos por Mês',
                        font: { size: 16 }
                    },
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        },
                        title: {
                            display: true,
                            text: 'Quantidade'
                        }
                    }
                }
            }
        });

        return this.charts[containerId];
    }

    /**
     * Cria gráfico de pizza para status da anamnese
     */
    criarGraficoStatusAnamnese(containerId, anamneses) {
        const ctx = document.getElementById(containerId);
        if (!ctx) return null;

        if (this.charts[containerId]) {
            this.charts[containerId].destroy();
        }

        // Conta status
        const statusCount = {};
        anamneses.forEach(a => {
            const status = a.statusAnm || 'Não informado';
            statusCount[status] = (statusCount[status] || 0) + 1;
        });

        const labels = Object.keys(statusCount);
        const data = Object.values(statusCount);
        const colors = [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)'
        ];

        this.charts[containerId] = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: colors.slice(0, labels.length),
                    borderColor: '#fff',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Distribuição de Status - Anamnese',
                        font: { size: 16 }
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    }
                }
            }
        });

        return this.charts[containerId];
    }

    /**
     * Cria gráfico de dor (escala 0-10) ao longo do tempo
     */
    criarGraficoDor(containerId, atendimentos) {
        const ctx = document.getElementById(containerId);
        if (!ctx) return null;

        if (this.charts[containerId]) {
            this.charts[containerId].destroy();
        }

        const datas = atendimentos.map(a => new Date(a.dataAtendi).toLocaleDateString('pt-BR'));
        // Assumindo que existe um campo de dor (você pode ajustar conforme sua estrutura)
        const nivelDor = atendimentos.map((a, i) => Math.floor(Math.random() * 10)); // MOCK - substitua pelo campo real

        this.charts[containerId] = new Chart(ctx, {
            type: 'line',
            data: {
                labels: datas,
                datasets: [{
                    label: 'Nível de Dor (0-10)',
                    data: nivelDor,
                    borderColor: '#ff6384',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    tension: 0.3,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Evolução da Dor',
                        font: { size: 16 }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 10,
                        ticks: {
                            stepSize: 1
                        },
                        title: {
                            display: true,
                            text: 'Escala de Dor'
                        }
                    }
                }
            }
        });

        return this.charts[containerId];
    }

    /**
     * Destrói todos os gráficos
     */
    destruirTodos() {
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
        this.charts = {};
    }

    /**
     * Destrói um gráfico específico
     */
    destruir(containerId) {
        if (this.charts[containerId]) {
            this.charts[containerId].destroy();
            delete this.charts[containerId];
        }
    }
}

// Instância global
const chartsManager = new ChartsManager();

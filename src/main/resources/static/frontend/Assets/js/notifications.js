/**
 * Sistema de Notificações Toast - FisioClin
 * 
 * Uso:
 *   notifications.success('Operação realizada com sucesso!');
 *   notifications.error('Erro ao processar');
 *   notifications.warning('Atenção: paciente inativo');
 *   notifications.info('Dica: use filtros para buscar');
 */

class NotificationSystem {
    constructor() {
        this.container = null;
        this.queue = [];
        this.maxVisible = 5;
        this.init();
    }

    init() {
        // Criar container de notificações se não existir
        if (!document.getElementById('notification-container')) {
            this.container = document.createElement('div');
            this.container.id = 'notification-container';
            this.container.className = 'notification-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('notification-container');
        }

        // Adicionar estilos
        this.injectStyles();
    }

    injectStyles() {
        if (document.getElementById('notification-styles')) return;

        const styles = document.createElement('style');
        styles.id = 'notification-styles';
        styles.textContent = `
            .notification-container {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                display: flex;
                flex-direction: column;
                gap: 10px;
                max-width: 400px;
                pointer-events: none;
            }

            .notification {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                padding: 16px 20px;
                border-radius: 12px;
                background: white;
                box-shadow: 0 10px 40px rgba(0,0,0,0.15), 0 2px 10px rgba(0,0,0,0.1);
                animation: slideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                pointer-events: auto;
                transform-origin: right center;
                border-left: 4px solid;
                backdrop-filter: blur(10px);
            }

            .notification.removing {
                animation: slideOut 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
            }

            @keyframes slideIn {
                from {
                    opacity: 0;
                    transform: translateX(100%);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            @keyframes slideOut {
                from {
                    opacity: 1;
                    transform: translateX(0);
                }
                to {
                    opacity: 0;
                    transform: translateX(100%);
                }
            }

            .notification-icon {
                flex-shrink: 0;
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
            }

            .notification-icon svg {
                width: 16px;
                height: 16px;
                stroke-width: 2.5;
            }

            .notification-content {
                flex: 1;
                min-width: 0;
            }

            .notification-title {
                font-weight: 600;
                font-size: 14px;
                margin-bottom: 2px;
                color: #1a1a2e;
            }

            .notification-message {
                font-size: 13px;
                color: #6b7280;
                line-height: 1.4;
                word-wrap: break-word;
            }

            .notification-close {
                flex-shrink: 0;
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                background: transparent;
                border: none;
                cursor: pointer;
                color: #9ca3af;
                border-radius: 4px;
                transition: all 0.15s ease;
            }

            .notification-close:hover {
                background: rgba(0,0,0,0.05);
                color: #4b5563;
            }

            .notification-progress {
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                height: 3px;
                background: rgba(0,0,0,0.1);
                border-radius: 0 0 12px 12px;
                overflow: hidden;
            }

            .notification-progress-bar {
                height: 100%;
                transition: width linear;
            }

            /* Tipos de notificação */
            .notification.success {
                border-left-color: #10b981;
            }
            .notification.success .notification-icon {
                background: #d1fae5;
                color: #059669;
            }
            .notification.success .notification-progress-bar {
                background: #10b981;
            }

            .notification.error {
                border-left-color: #ef4444;
            }
            .notification.error .notification-icon {
                background: #fee2e2;
                color: #dc2626;
            }
            .notification.error .notification-progress-bar {
                background: #ef4444;
            }

            .notification.warning {
                border-left-color: #f59e0b;
            }
            .notification.warning .notification-icon {
                background: #fef3c7;
                color: #d97706;
            }
            .notification.warning .notification-progress-bar {
                background: #f59e0b;
            }

            .notification.info {
                border-left-color: #3b82f6;
            }
            .notification.info .notification-icon {
                background: #dbeafe;
                color: #2563eb;
            }
            .notification.info .notification-progress-bar {
                background: #3b82f6;
            }

            /* Responsivo */
            @media (max-width: 480px) {
                .notification-container {
                    left: 10px;
                    right: 10px;
                    max-width: none;
                }
            }
        `;
        document.head.appendChild(styles);
    }

    getIcon(type) {
        const icons = {
            success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><polyline points="20 6 9 17 4 12"></polyline></svg>',
            error: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>',
            warning: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>',
            info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>'
        };
        return icons[type] || icons.info;
    }

    getTitle(type) {
        const titles = {
            success: 'Sucesso',
            error: 'Erro',
            warning: 'Atenção',
            info: 'Informação'
        };
        return titles[type] || 'Notificação';
    }

    show(options) {
        const {
            type = 'info',
            title,
            message,
            duration = 5000,
            closable = true
        } = options;

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.style.position = 'relative';

        notification.innerHTML = `
            <div class="notification-icon">
                ${this.getIcon(type)}
            </div>
            <div class="notification-content">
                <div class="notification-title">${title || this.getTitle(type)}</div>
                <div class="notification-message">${this.escapeHtml(message)}</div>
            </div>
            ${closable ? `
                <button class="notification-close" aria-label="Fechar">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            ` : ''}
            <div class="notification-progress">
                <div class="notification-progress-bar" style="width: 100%;"></div>
            </div>
        `;

        // Adicionar ao container
        this.container.appendChild(notification);

        // Progress bar animation
        const progressBar = notification.querySelector('.notification-progress-bar');
        setTimeout(() => {
            progressBar.style.width = '0%';
            progressBar.style.transitionDuration = `${duration}ms`;
        }, 10);

        // Fechar ao clicar
        if (closable) {
            const closeBtn = notification.querySelector('.notification-close');
            closeBtn.addEventListener('click', () => this.remove(notification));
        }

        // Auto-remover após duração
        const timeoutId = setTimeout(() => {
            this.remove(notification);
        }, duration);

        // Pausar ao hover
        notification.addEventListener('mouseenter', () => {
            clearTimeout(timeoutId);
            progressBar.style.transitionDuration = '0ms';
        });

        notification.addEventListener('mouseleave', () => {
            const remaining = (parseFloat(progressBar.style.width) / 100) * duration;
            progressBar.style.transitionDuration = `${remaining}ms`;
            progressBar.style.width = '0%';
            setTimeout(() => this.remove(notification), remaining);
        });

        return notification;
    }

    remove(notification) {
        if (!notification || notification.classList.contains('removing')) return;
        
        notification.classList.add('removing');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Métodos de conveniência
    success(message, options = {}) {
        return this.show({ type: 'success', message, ...options });
    }

    error(message, options = {}) {
        return this.show({ type: 'error', message, duration: 8000, ...options });
    }

    warning(message, options = {}) {
        return this.show({ type: 'warning', message, duration: 6000, ...options });
    }

    info(message, options = {}) {
        return this.show({ type: 'info', message, ...options });
    }

    // Limpar todas as notificações
    clearAll() {
        const notifications = this.container.querySelectorAll('.notification');
        notifications.forEach(n => this.remove(n));
    }
}

// Criar instância global
const notifications = new NotificationSystem();

// Exportar para uso em módulos ES6
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { NotificationSystem, notifications };
}


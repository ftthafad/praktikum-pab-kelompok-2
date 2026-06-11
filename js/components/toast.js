/* ═══════════════════════════════════════════════════════
   Toast Notification Component
   ═══════════════════════════════════════════════════════ */

const Toast = (() => {
    function show(message, type = 'info', duration = 3500) {
        const container = document.getElementById('toast-container');
        const iconMap = {
            success: 'check_circle',
            error: 'error',
            info: 'info',
        };

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <span class="material-icons-round">${iconMap[type] || 'info'}</span>
            <span class="toast-text">${message}</span>
            <span class="material-icons-round toast-close" onclick="this.parentElement.remove()">close</span>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            toast.classList.add('exiting');
            setTimeout(() => toast.remove(), 250);
        }, duration);
    }

    function success(msg) { show(msg, 'success'); }
    function error(msg) { show(msg, 'error'); }
    function info(msg) { show(msg, 'info'); }

    return { show, success, error, info };
})();

/* ═══════════════════════════════════════════════════════
   Modal / Dialog Component
   ═══════════════════════════════════════════════════════ */

const Modal = (() => {
    function show({ icon, iconClass = 'modal-icon-info', title, body, confirmText = 'OK', cancelText = 'Batal', onConfirm, onCancel, dangerConfirm = false }) {
        const overlay = document.getElementById('modal-overlay');
        overlay.classList.remove('hidden');

        overlay.innerHTML = `
            <div class="modal">
                ${icon ? `
                <div class="modal-icon ${iconClass}">
                    <span class="material-icons-round">${icon}</span>
                </div>` : ''}
                <h3 class="modal-title">${title}</h3>
                <p class="modal-body">${body}</p>
                <div class="modal-actions">
                    ${cancelText ? `<button class="btn btn-ghost" id="modal-cancel">${cancelText}</button>` : ''}
                    <button class="btn ${dangerConfirm ? 'btn-danger' : 'btn-primary'}" id="modal-confirm">${confirmText}</button>
                </div>
            </div>
        `;

        const confirmBtn = document.getElementById('modal-confirm');
        const cancelBtn = document.getElementById('modal-cancel');

        confirmBtn.addEventListener('click', () => {
            hide();
            if (onConfirm) onConfirm();
        });

        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                hide();
                if (onCancel) onCancel();
            });
        }

        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                hide();
                if (onCancel) onCancel();
            }
        });
    }

    function hide() {
        const overlay = document.getElementById('modal-overlay');
        overlay.classList.add('hidden');
        overlay.innerHTML = '';
    }

    // ── Prompt with textarea ─────────────────────────
    function prompt({ title, body, placeholder = '', confirmText = 'Kirim', onConfirm }) {
        const overlay = document.getElementById('modal-overlay');
        overlay.classList.remove('hidden');

        overlay.innerHTML = `
            <div class="modal" style="text-align:left;">
                <h3 class="modal-title">${title}</h3>
                <p class="modal-body" style="margin-bottom:12px;">${body}</p>
                <div class="form-group" style="margin-bottom:16px;">
                    <textarea class="form-input form-textarea" id="modal-input" placeholder="${placeholder}" rows="3"></textarea>
                </div>
                <div class="modal-actions" style="justify-content:flex-end;">
                    <button class="btn btn-ghost" id="modal-cancel">Batal</button>
                    <button class="btn btn-primary" id="modal-confirm">${confirmText}</button>
                </div>
            </div>
        `;

        document.getElementById('modal-confirm').addEventListener('click', () => {
            const val = document.getElementById('modal-input').value.trim();
            hide();
            if (onConfirm) onConfirm(val);
        });

        document.getElementById('modal-cancel').addEventListener('click', hide);
        overlay.addEventListener('click', (e) => { if (e.target === overlay) hide(); });
    }

    return { show, hide, prompt };
})();

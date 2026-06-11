/* ═══════════════════════════════════════════════════════
   Pengajuan Pengelola Page
   Mirrors FormPengajuanScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const PengajuanPage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn()) {
            App.navigate('#/login');
            return;
        }

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <div class="form-page">
                    <div class="form-page-title">
                        <button class="btn btn-icon btn-ghost" onclick="history.back()">
                            <span class="material-icons-round">arrow_back</span>
                        </button>
                        Ajukan Jadi Pengelola
                    </div>

                    <div id="pengajuan-content">
                        <div class="page-loader"><div class="spinner"></div></div>
                    </div>
                </div>
            </div>
        `;

        // Check existing pengajuan status
        try {
            const res = await API.getPengajuanStatus();
            const pengajuan = res.data;

            if (pengajuan) {
                renderStatus(pengajuan);
                return;
            }
        } catch (e) {
            // No existing pengajuan — show form
        }

        renderForm();
    }

    function renderStatus(pengajuan) {
        const el = document.getElementById('pengajuan-content');
        if (!el) return;

        const statusConfig = {
            pending: { icon: 'hourglass_empty', color: 'var(--pending)', bg: 'var(--pending-light)', label: 'Menunggu Persetujuan', text: 'Pengajuanmu sedang ditinjau oleh admin. Kami akan memberitahu hasilnya.' },
            approved: { icon: 'check_circle', color: 'var(--success)', bg: 'var(--success-light)', label: 'Disetujui', text: 'Selamat! Pengajuanmu telah disetujui. Kamu sekarang bisa mengelola wisata.' },
            rejected: { icon: 'cancel', color: 'var(--error)', bg: 'var(--error-light)', label: 'Ditolak', text: 'Maaf, pengajuanmu ditolak.' },
        };

        const cfg = statusConfig[pengajuan.status] || statusConfig.pending;

        el.innerHTML = `
            <div class="pengajuan-status-card">
                <div class="pengajuan-status-icon" style="background:${cfg.bg};">
                    <span class="material-icons-round" style="color:${cfg.color};">${cfg.icon}</span>
                </div>
                <h3 class="text-h3" style="margin-bottom:8px;">${cfg.label}</h3>
                <p class="text-body" style="color:var(--text-secondary);margin-bottom:16px;">${cfg.text}</p>

                ${pengajuan.catatan_admin ? `
                    <div style="background:rgba(var(--primary-rgb),0.05);border-radius:var(--radius-md);padding:16px;text-align:left;margin-top:8px;">
                        <p class="text-label" style="color:var(--text-secondary);margin-bottom:4px;">Catatan Admin:</p>
                        <p class="text-body" style="color:var(--text-primary);">${esc(pengajuan.catatan_admin)}</p>
                    </div>
                ` : ''}

                <div style="margin-top:20px;">
                    <div style="text-align:left; display:grid; gap:8px;">
                        <div><span class="text-label" style="color:var(--text-secondary);">Nama Usaha:</span> <span class="text-body">${esc(pengajuan.nama_usaha)}</span></div>
                        <div><span class="text-label" style="color:var(--text-secondary);">Deskripsi:</span> <span class="text-body">${esc(pengajuan.deskripsi)}</span></div>
                        <div><span class="text-label" style="color:var(--text-secondary);">Alasan:</span> <span class="text-body">${esc(pengajuan.alasan)}</span></div>
                    </div>
                </div>
            </div>

            <button class="btn btn-ghost w-full" onclick="history.back()">
                <span class="material-icons-round">arrow_back</span> Kembali ke Profil
            </button>
        `;
    }

    function renderForm() {
        const el = document.getElementById('pengajuan-content');
        if (!el) return;

        el.innerHTML = `
            <div class="detail-card" style="margin-bottom:24px;">
                <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;">
                    <div style="width:48px;height:48px;border-radius:var(--radius-md);background:rgba(var(--primary-rgb),0.1);display:flex;align-items:center;justify-content:center;">
                        <span class="material-icons-round" style="color:var(--primary);font-size:24px;">store</span>
                    </div>
                    <div>
                        <h3 class="text-h3">Form Pengajuan</h3>
                        <p class="text-body-sm" style="color:var(--text-secondary);">Isi data untuk mengajukan diri sebagai pengelola wisata</p>
                    </div>
                </div>

                <form id="pengajuan-form">
                    <div class="form-group">
                        <label class="form-label" for="peng-nama">Nama Usaha</label>
                        <input type="text" class="form-input" id="peng-nama" placeholder="Nama usaha wisata Anda" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="peng-deskripsi">Deskripsi Usaha</label>
                        <textarea class="form-input form-textarea" id="peng-deskripsi" placeholder="Jelaskan usaha wisata Anda..." required rows="4"></textarea>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="peng-alasan">Alasan Pengajuan</label>
                        <textarea class="form-input form-textarea" id="peng-alasan" placeholder="Mengapa Anda ingin menjadi pengelola?" required rows="3"></textarea>
                    </div>

                    <div id="pengajuan-error" class="form-error" style="margin-bottom:12px;display:none;"></div>

                    <button type="submit" class="btn btn-primary btn-lg w-full" id="pengajuan-submit">
                        <span class="material-icons-round">send</span>
                        Kirim Pengajuan
                    </button>
                </form>
            </div>
        `;

        document.getElementById('pengajuan-form').addEventListener('submit', handleSubmit);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        const namaUsaha = document.getElementById('peng-nama').value.trim();
        const deskripsi = document.getElementById('peng-deskripsi').value.trim();
        const alasan = document.getElementById('peng-alasan').value.trim();
        const errorEl = document.getElementById('pengajuan-error');
        const submitBtn = document.getElementById('pengajuan-submit');

        errorEl.style.display = 'none';
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<div class="spinner" style="width:20px;height:20px;border-width:2px;"></div> Mengirim...';

        try {
            await API.submitPengajuan(namaUsaha, deskripsi, alasan);
            Toast.success('Pengajuan berhasil dikirim!');
            render(); // Reload to show status
        } catch (err) {
            errorEl.textContent = err.message || 'Gagal mengirim pengajuan';
            errorEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span class="material-icons-round">send</span> Kirim Pengajuan';
        }
    }

    function destroy() {}

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy };
})();

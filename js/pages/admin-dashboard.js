/* ═══════════════════════════════════════════════════════
   Admin Dashboard — Pengajuan Approval
   Mirrors DashboardApprovalScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const AdminDashboardPage = (() => {
    let allPengajuan = [];
    let activeTab = 'pending';

    async function render() {
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn() || Auth.getUserRole() !== 'super_admin') {
            App.navigate('#/login');
            return;
        }

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <!-- Admin Header -->
                <div class="admin-header">
                    <div class="container">
                        <div class="flex justify-between items-center">
                            <div>
                                <h1 class="text-h1" style="color:var(--white);margin-bottom:4px;">Dashboard Admin</h1>
                                <p class="text-body-sm" style="color:var(--primary-light);">Kelola pengajuan pengelola wisata</p>
                            </div>
                            <button class="btn btn-sm" style="background:rgba(255,255,255,0.15);color:var(--white);border:1px solid rgba(255,255,255,0.2);" id="admin-logout">
                                <span class="material-icons-round">logout</span> Keluar
                            </button>
                        </div>

                        <div class="stat-cards" id="stat-cards">
                            <div class="stat-card">
                                <div class="stat-card-value" id="stat-total">-</div>
                                <div class="stat-card-label">Total Pengajuan</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-value" style="color:var(--pending);" id="stat-pending">-</div>
                                <div class="stat-card-label">Menunggu</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-value" style="color:var(--success);" id="stat-approved">-</div>
                                <div class="stat-card-label">Disetujui</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-value" style="color:#f87171;" id="stat-rejected">-</div>
                                <div class="stat-card-label">Ditolak</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="container" style="padding-top:24px;padding-bottom:40px;">
                    <!-- Tabs -->
                    <div class="tabs" id="admin-tabs">
                        <div class="tab active" data-tab="pending">
                            <span class="material-icons-round" style="font-size:16px;vertical-align:middle;margin-right:4px;">hourglass_empty</span>
                            Menunggu
                        </div>
                        <div class="tab" data-tab="approved">
                            <span class="material-icons-round" style="font-size:16px;vertical-align:middle;margin-right:4px;">check_circle</span>
                            Disetujui
                        </div>
                        <div class="tab" data-tab="rejected">
                            <span class="material-icons-round" style="font-size:16px;vertical-align:middle;margin-right:4px;">cancel</span>
                            Ditolak
                        </div>
                    </div>

                    <!-- List -->
                    <div id="pengajuan-list" style="display:flex;flex-direction:column;gap:16px;">
                        <div class="page-loader"><div class="spinner"></div></div>
                    </div>
                </div>
            </div>
        `;

        // Tab switching
        document.getElementById('admin-tabs').addEventListener('click', (e) => {
            const tab = e.target.closest('.tab');
            if (tab) {
                activeTab = tab.dataset.tab;
                document.querySelectorAll('#admin-tabs .tab').forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                renderList();
            }
        });

        // Logout
        document.getElementById('admin-logout').addEventListener('click', () => {
            Modal.show({
                icon: 'logout',
                iconClass: 'modal-icon-danger',
                title: 'Keluar dari Akun?',
                body: 'Kamu akan keluar dari dashboard admin.',
                confirmText: 'Keluar',
                cancelText: 'Batal',
                dangerConfirm: true,
                onConfirm: async () => {
                    try { await API.logout(); } catch (e) { /* ignore */ }
                    Auth.logout();
                    App.navigate('#/login');
                }
            });
        });

        await loadData();
    }

    async function loadData() {
        try {
            const res = await API.getSemuaPengajuan();
            allPengajuan = res.data || [];

            // Update stats
            const total = allPengajuan.length;
            const pending = allPengajuan.filter(p => p.status === 'pending').length;
            const approved = allPengajuan.filter(p => p.status === 'approved').length;
            const rejected = allPengajuan.filter(p => p.status === 'rejected').length;

            setText('stat-total', total);
            setText('stat-pending', pending);
            setText('stat-approved', approved);
            setText('stat-rejected', rejected);

            renderList();
        } catch (err) {
            document.getElementById('pengajuan-list').innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">😕</div>
                    <div class="empty-state-title">Gagal memuat data</div>
                    <p class="empty-state-text">${err.message || 'Periksa koneksi internet'}</p>
                </div>
            `;
        }
    }

    function renderList() {
        const listEl = document.getElementById('pengajuan-list');
        if (!listEl) return;

        const filtered = allPengajuan.filter(p => p.status === activeTab);

        if (filtered.length === 0) {
            listEl.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">${activeTab === 'pending' ? '📋' : activeTab === 'approved' ? '✅' : '❌'}</div>
                    <div class="empty-state-title">Tidak ada pengajuan ${activeTab}</div>
                </div>
            `;
            return;
        }

        listEl.innerHTML = filtered.map(p => renderPengajuanCard(p)).join('');
    }

    function renderPengajuanCard(p) {
        const statusConfig = {
            pending: { class: 'status-pending', label: 'Menunggu' },
            approved: { class: 'status-approved', label: 'Disetujui' },
            rejected: { class: 'status-rejected', label: 'Ditolak' },
        };
        const sc = statusConfig[p.status] || statusConfig.pending;

        return `
            <div class="pengajuan-card">
                <div class="flex justify-between items-start" style="margin-bottom:12px;">
                    <div>
                        <h3 class="text-h3">${esc(p.nama_usaha)}</h3>
                        <div class="flex items-center gap-sm" style="margin-top:4px;">
                            <span class="text-body-sm" style="color:var(--text-secondary);">
                                <span class="material-icons-round" style="font-size:14px;vertical-align:middle;">person</span>
                                ${esc(p.user?.name || 'Unknown')}
                            </span>
                            <span class="text-body-sm" style="color:var(--text-muted);">
                                ${esc(p.user?.email || '')}
                            </span>
                        </div>
                    </div>
                    <span class="pengajuan-status ${sc.class}">${sc.label}</span>
                </div>

                <div style="margin-bottom:12px;">
                    <p class="text-label" style="color:var(--text-secondary);margin-bottom:2px;">Deskripsi:</p>
                    <p class="text-body-sm">${esc(p.deskripsi)}</p>
                </div>

                <div style="margin-bottom:12px;">
                    <p class="text-label" style="color:var(--text-secondary);margin-bottom:2px;">Alasan:</p>
                    <p class="text-body-sm">${esc(p.alasan)}</p>
                </div>

                ${p.catatan_admin ? `
                <div style="background:rgba(var(--primary-rgb),0.05);border-radius:var(--radius-sm);padding:12px;margin-bottom:12px;">
                    <p class="text-label" style="color:var(--text-secondary);margin-bottom:2px;">Catatan Admin:</p>
                    <p class="text-body-sm">${esc(p.catatan_admin)}</p>
                </div>
                ` : ''}

                ${p.created_at ? `
                <p class="text-caption" style="color:var(--text-muted);margin-bottom:12px;">
                    Diajukan: ${new Date(p.created_at).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' })}
                </p>
                ` : ''}

                ${p.status === 'pending' ? `
                <div class="flex gap-sm">
                    <button class="btn btn-sm btn-primary" onclick="AdminDashboardPage.handleApprove(${p.id})">
                        <span class="material-icons-round">check</span> Setujui
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="AdminDashboardPage.handleReject(${p.id})">
                        <span class="material-icons-round">close</span> Tolak
                    </button>
                </div>
                ` : ''}
            </div>
        `;
    }

    async function handleApprove(id) {
        Modal.show({
            icon: 'check_circle',
            iconClass: 'modal-icon-success',
            title: 'Setujui Pengajuan?',
            body: 'Pengguna akan mendapatkan akses sebagai pengelola wisata.',
            confirmText: 'Setujui',
            cancelText: 'Batal',
            onConfirm: async () => {
                try {
                    await API.approvePengajuan(id);
                    Toast.success('Pengajuan disetujui!');
                    await loadData();
                } catch (err) {
                    Toast.error(err.message || 'Gagal menyetujui');
                }
            }
        });
    }

    function handleReject(id) {
        Modal.prompt({
            title: 'Tolak Pengajuan',
            body: 'Berikan alasan penolakan:',
            placeholder: 'Tuliskan catatan untuk pemohon...',
            confirmText: 'Tolak',
            onConfirm: async (catatan) => {
                if (!catatan) {
                    Toast.error('Catatan penolakan harus diisi');
                    return;
                }
                try {
                    await API.rejectPengajuan(id, catatan);
                    Toast.success('Pengajuan ditolak');
                    await loadData();
                } catch (err) {
                    Toast.error(err.message || 'Gagal menolak');
                }
            }
        });
    }

    function destroy() {
        activeTab = 'pending';
    }

    function setText(id, val) {
        const el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy, handleApprove, handleReject };
})();

/* ═══════════════════════════════════════════════════════
   Pengelola Page — My Wisata List
   Mirrors DaftarWisataSayaScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const PengelolaPage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn() || Auth.getUserRole() !== 'pengelola') {
            App.navigate('#/profile');
            return;
        }

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <div class="container" style="padding-top:32px;padding-bottom:40px;">
                    <div class="flex justify-between items-center" style="margin-bottom:24px;">
                        <div class="flex items-center gap-md">
                            <button class="btn btn-icon btn-ghost" onclick="App.navigate('#/profile')">
                                <span class="material-icons-round">arrow_back</span>
                            </button>
                            <div>
                                <h1 class="text-h1">Wisata Saya</h1>
                                <p class="text-body-sm" style="color:var(--text-secondary);">Kelola destinasi wisata milikmu</p>
                            </div>
                        </div>
                        <button class="btn btn-primary" onclick="App.navigate('#/wisata/new')">
                            <span class="material-icons-round">add</span> Tambah
                        </button>
                    </div>

                    <div id="wisata-saya-list" style="display:flex;flex-direction:column;gap:16px;">
                        <div class="page-loader"><div class="spinner"></div></div>
                    </div>
                </div>
            </div>
        `;

        await loadWisata();
    }

    async function loadWisata() {
        const listEl = document.getElementById('wisata-saya-list');
        if (!listEl) return;

        try {
            const res = await API.getWisataSaya();
            const wisataList = res.data || [];

            if (wisataList.length === 0) {
                listEl.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">🏖️</div>
                        <div class="empty-state-title">Belum ada wisata</div>
                        <p class="empty-state-text">Mulai tambahkan destinasi wisata yang kamu kelola</p>
                        <button class="btn btn-primary" onclick="App.navigate('#/wisata/new')" style="margin-top:16px;">
                            <span class="material-icons-round">add</span> Tambah Wisata
                        </button>
                    </div>
                `;
                return;
            }

            listEl.innerHTML = wisataList.map(w => renderWisataItem(w)).join('');
        } catch (err) {
            listEl.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">😕</div>
                    <div class="empty-state-title">Gagal memuat data</div>
                    <p class="empty-state-text">${err.message || 'Periksa koneksi internet'}</p>
                </div>
            `;
        }
    }

    function renderWisataItem(wisata) {
        const imageUrl = wisata.cover_photo?.photo_url || (wisata.photos && wisata.photos[0]?.photo_url) || '';

        return `
            <div class="card" style="padding:0;">
                <div class="flex" style="min-height:120px;">
                    ${imageUrl
                        ? `<img src="${imageUrl}" alt="${esc(wisata.name)}" style="width:160px;min-width:160px;height:120px;object-fit:cover;" onerror="this.style.background='linear-gradient(135deg,#9CD5FF,#7AAACE)';this.src=''">`
                        : `<div style="width:160px;min-width:160px;height:120px;background:linear-gradient(135deg,#9CD5FF,#7AAACE);display:flex;align-items:center;justify-content:center;">
                            <span class="material-icons-round" style="font-size:36px;color:var(--primary);opacity:0.5;">landscape</span>
                           </div>`
                    }
                    <div style="flex:1;padding:16px;display:flex;flex-direction:column;justify-content:space-between;">
                        <div>
                            <h3 class="text-h3" style="margin-bottom:4px;">${esc(wisata.name)}</h3>
                            <div class="flex items-center gap-xs" style="color:var(--text-secondary);">
                                <span class="material-icons-round" style="font-size:14px;">location_on</span>
                                <span class="text-body-sm">${esc(wisata.location)}</span>
                            </div>
                        </div>
                        <div class="flex gap-sm" style="margin-top:12px;">
                            <button class="btn btn-sm btn-secondary" onclick="App.navigate('#/wisata/edit/${wisata.id}')">
                                <span class="material-icons-round">edit</span> Edit
                            </button>
                            <button class="btn btn-sm btn-ghost" style="color:var(--error);" onclick="PengelolaPage.confirmDelete(${wisata.id}, '${esc(wisata.name)}')">
                                <span class="material-icons-round">delete</span> Hapus
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    function confirmDelete(wisataId, name) {
        Modal.show({
            icon: 'delete',
            iconClass: 'modal-icon-danger',
            title: 'Hapus Wisata?',
            body: `Wisata <strong>${name}</strong> akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.`,
            confirmText: 'Hapus',
            cancelText: 'Batal',
            dangerConfirm: true,
            onConfirm: async () => {
                try {
                    await API.deleteWisata(wisataId);
                    Toast.success('Wisata berhasil dihapus');
                    loadWisata();
                } catch (err) {
                    Toast.error(err.message || 'Gagal menghapus wisata');
                }
            }
        });
    }

    function destroy() {}

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy, confirmDelete };
})();

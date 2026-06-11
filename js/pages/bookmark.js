/* ═══════════════════════════════════════════════════════
   Bookmark Page
   Mirrors BookmarkScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const BookmarkPage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn()) {
            content.innerHTML = `
                <div class="page-wrapper">
                    <div class="empty-state" style="padding-top:120px;">
                        <div class="empty-state-icon">🔒</div>
                        <div class="empty-state-title">Login Diperlukan</div>
                        <p class="empty-state-text">Masuk ke akunmu untuk melihat wisata yang disimpan</p>
                        <button class="btn btn-primary" onclick="App.navigate('#/login')" style="margin-top:16px;">
                            <span class="material-icons-round">login</span> Masuk
                        </button>
                    </div>
                </div>
            `;
            return;
        }

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <div class="container" style="padding-top:32px;padding-bottom:40px;">
                    <div style="margin-bottom:24px;">
                        <h1 class="text-h1">
                            <span class="material-icons-round" style="vertical-align:middle;color:var(--primary);margin-right:8px;">bookmark</span>
                            Wisata Tersimpan
                        </h1>
                        <p class="text-body-sm" style="color:var(--text-secondary);margin-top:4px;">Koleksi wisata favoritmu</p>
                    </div>

                    <div id="bookmark-grid" class="bookmark-grid">
                        ${WisataCard.skeletonCards(4)}
                    </div>
                </div>
            </div>
        `;

        await loadBookmarks();
    }

    async function loadBookmarks() {
        const grid = document.getElementById('bookmark-grid');
        if (!grid) return;

        try {
            const res = await API.getBookmarks();
            const bookmarks = res.data || [];

            if (bookmarks.length === 0) {
                grid.innerHTML = `
                    <div class="empty-state" style="grid-column:1/-1;">
                        <div class="empty-state-icon">📌</div>
                        <div class="empty-state-title">Belum ada bookmark</div>
                        <p class="empty-state-text">Simpan wisata favoritmu untuk dilihat nanti</p>
                        <button class="btn btn-primary" onclick="App.navigate('#/explore')" style="margin-top:16px;">
                            <span class="material-icons-round">explore</span> Jelajahi Wisata
                        </button>
                    </div>
                `;
                return;
            }

            grid.innerHTML = bookmarks.map(bm => WisataCard.card(bm.wisata)).join('');
        } catch (err) {
            console.error('Bookmarks load failed:', err);
            grid.innerHTML = `
                <div class="empty-state" style="grid-column:1/-1;">
                    <div class="empty-state-icon">😕</div>
                    <div class="empty-state-title">Gagal memuat bookmark</div>
                    <p class="empty-state-text">${err.message || 'Periksa koneksi internet'}</p>
                </div>
            `;
        }
    }

    function destroy() {}

    return { render, destroy };
})();

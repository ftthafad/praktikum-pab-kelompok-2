/* ═══════════════════════════════════════════════════════
   Explore Page — Search & Filter
   Mirrors ExploreScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const ExplorePage = (() => {
    let allWisata = [];
    let categories = [];
    let selectedCategoryId = null;
    let searchQuery = '';

    async function render() {
        const content = document.getElementById('page-content');

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <!-- Search Hero -->
                <div class="search-hero">
                    <div class="container">
                        <h2 style="color:var(--white);font-weight:700;margin-bottom:12px;">
                            <span class="material-icons-round" style="vertical-align:middle;margin-right:4px;">explore</span>
                            Jelajahi Wisata
                        </h2>
                        <div class="search-input-wrapper">
                            <span class="material-icons-round">search</span>
                            <input type="text" id="explore-search" placeholder="Cari destinasi wisata..." autocomplete="off">
                        </div>
                    </div>
                </div>

                <div class="container">
                    <!-- Category Filters -->
                    <div class="chips-row" id="explore-chips" style="padding:16px 0;">
                        <span class="chip chip-active" data-cat="">Semua</span>
                    </div>

                    <!-- Result count -->
                    <p id="explore-count" class="text-body-sm" style="color:var(--text-secondary);margin-bottom:12px;"></p>

                    <!-- Wisata Grid -->
                    <div class="wisata-grid" id="explore-grid" style="margin-bottom:40px;">
                        ${WisataCard.skeletonCards(6)}
                    </div>
                </div>
            </div>
        `;

        // Attach search listener
        const searchInput = document.getElementById('explore-search');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                searchQuery = e.target.value;
                applyFilters();
            });
        }

        // Load data
        await loadData();
    }

    async function loadData() {
        try {
            const [catRes, wisataRes] = await Promise.all([
                API.getCategories().catch(() => ({ data: [] })),
                API.getWisata().catch(() => ({ data: [] }))
            ]);

            categories = catRes.data || [];
            allWisata = wisataRes.data || [];

            renderChips();
            applyFilters();
        } catch (err) {
            console.error('Explore load failed:', err);
            document.getElementById('explore-grid').innerHTML = `
                <div class="empty-state" style="grid-column:1/-1;">
                    <div class="empty-state-icon">😕</div>
                    <div class="empty-state-title">Gagal memuat data</div>
                    <p class="empty-state-text">Periksa koneksi internet Anda</p>
                </div>
            `;
        }
    }

    function renderChips() {
        const chipsEl = document.getElementById('explore-chips');
        if (!chipsEl) return;

        chipsEl.innerHTML = `
            <span class="chip ${selectedCategoryId === null ? 'chip-active' : 'chip-default'}" data-cat="">Semua</span>
            ${categories.map(c => `
                <span class="chip ${selectedCategoryId === c.id ? 'chip-active' : 'chip-default'}" data-cat="${c.id}">${esc(c.name)}</span>
            `).join('')}
        `;

        chipsEl.querySelectorAll('.chip').forEach(chip => {
            chip.addEventListener('click', () => {
                selectedCategoryId = chip.dataset.cat ? parseInt(chip.dataset.cat) : null;
                renderChips();
                applyFilters();
            });
        });
    }

    function applyFilters() {
        let filtered = [...allWisata];

        // Category filter
        if (selectedCategoryId !== null) {
            filtered = filtered.filter(w => w.category_id === selectedCategoryId);
        }

        // Search filter
        if (searchQuery.trim()) {
            const q = searchQuery.toLowerCase();
            filtered = filtered.filter(w =>
                w.name.toLowerCase().includes(q) ||
                w.location.toLowerCase().includes(q) ||
                (w.category?.name || '').toLowerCase().includes(q)
            );
        }

        // Update count
        const countEl = document.getElementById('explore-count');
        if (countEl) countEl.textContent = `${filtered.length} destinasi ditemukan`;

        // Render grid
        const grid = document.getElementById('explore-grid');
        if (grid) {
            grid.innerHTML = filtered.length > 0
                ? filtered.map(w => WisataCard.card(w)).join('')
                : `<div class="empty-state" style="grid-column:1/-1;">
                    <div class="empty-state-icon">😕</div>
                    <div class="empty-state-title">Wisata tidak ditemukan</div>
                    <p class="empty-state-text">Coba ubah kata kunci atau filter kategori</p>
                   </div>`;
        }
    }

    function destroy() {
        selectedCategoryId = null;
        searchQuery = '';
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy };
})();

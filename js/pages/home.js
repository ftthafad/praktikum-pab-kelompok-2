/* ═══════════════════════════════════════════════════════
   Home Page
   Mirrors HomeScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const HomePage = (() => {
    let bannerIndex = 0;
    let bannerInterval = null;

    const bannerImages = [
        'https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=1200&q=80',
        'https://images.unsplash.com/photo-1555400038-63f5ba517a47?w=1200&q=80',
        'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1200&q=80',
    ];

    const bannerTexts = [
        { title: 'Destinasi Wisata Unggulan', sub: 'Temukan keindahan tersembunyi di Jawa Tengah' },
        { title: 'Petualangan Menanti', sub: 'Jelajahi alam, budaya, dan kuliner terbaik' },
        { title: 'Pengalaman Tak Terlupakan', sub: 'Buat kenangan indah bersama TravelWaka' },
    ];

    async function render() {
        const content = document.getElementById('page-content');

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <!-- Hero Banner -->
                <div class="hero-banner" id="hero-banner">
                    ${bannerImages.map((img, i) => `
                        <div class="hero-banner-slide ${i === 0 ? 'active' : ''}" data-slide="${i}">
                            <img src="${img}" alt="Banner ${i + 1}" loading="${i === 0 ? 'eager' : 'lazy'}">
                            <div class="hero-banner-overlay"></div>
                            <div class="hero-banner-content">
                                <h1>${bannerTexts[i].title}</h1>
                                <p>${bannerTexts[i].sub}</p>
                            </div>
                        </div>
                    `).join('')}
                    <div class="hero-dots" id="hero-dots">
                        ${bannerImages.map((_, i) => `<div class="hero-dot ${i === 0 ? 'active' : ''}" data-dot="${i}"></div>`).join('')}
                    </div>
                </div>

                <div class="container">
                    <!-- Categories -->
                    <div style="margin-top:32px;">
                        <h2 class="section-title">Kategori</h2>
                        <div class="chips-row" id="category-chips" style="margin-top:12px;">
                            <span class="chip chip-active" data-cat="">Semua</span>
                            <span class="skeleton" style="width:80px;height:32px;border-radius:9999px;"></span>
                            <span class="skeleton" style="width:80px;height:32px;border-radius:9999px;"></span>
                            <span class="skeleton" style="width:80px;height:32px;border-radius:9999px;"></span>
                        </div>
                    </div>

                    <!-- Popular Wisata -->
                    <div style="margin-top:32px;">
                        <div class="section-header">
                            <h2 class="section-title">Wisata Populer</h2>
                            <a href="#/explore" class="section-link">
                                Lihat Semua <span class="material-icons-round">arrow_forward</span>
                            </a>
                        </div>
                        <div class="hscroll" id="popular-wisata">
                            ${WisataCard.skeletonCards(4)}
                        </div>
                    </div>

                    <!-- All Wisata -->
                    <div style="margin-top:40px; margin-bottom:40px;">
                        <h2 class="section-title" style="margin-bottom:16px;">Semua Wisata</h2>
                        <div id="all-wisata" style="display:flex;flex-direction:column;gap:12px;">
                            ${WisataCard.skeletonList(4)}
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Start banner carousel
        startBanner();

        // Dot navigation
        document.getElementById('hero-dots').addEventListener('click', (e) => {
            if (e.target.classList.contains('hero-dot')) {
                const idx = parseInt(e.target.dataset.dot);
                goToBanner(idx);
            }
        });

        // Load data
        loadData();
    }

    async function loadData() {
        try {
            // Load categories and wisata in parallel
            const [catRes, wisataRes] = await Promise.all([
                API.getCategories().catch(() => ({ data: [] })),
                API.getWisata().catch(() => ({ data: [] }))
            ]);

            const categories = catRes.data || [];
            const wisataList = wisataRes.data || [];

            // Render categories
            renderCategories(categories, wisataList);

            // Render popular (top 6 by rating)
            const popular = [...wisataList].sort((a, b) => b.rating - a.rating).slice(0, 6);
            const popularEl = document.getElementById('popular-wisata');
            if (popularEl) {
                popularEl.innerHTML = popular.length > 0
                    ? popular.map(w => WisataCard.card(w)).join('')
                    : '<div class="empty-state"><p class="empty-state-text">Belum ada wisata</p></div>';
            }

            // Render all wisata
            renderAllWisata(wisataList);
        } catch (err) {
            console.error('Home data load failed:', err);
        }
    }

    function renderCategories(categories, wisataList) {
        const chipsEl = document.getElementById('category-chips');
        if (!chipsEl) return;

        let selectedCat = '';

        function updateChips() {
            chipsEl.innerHTML = `
                <span class="chip ${selectedCat === '' ? 'chip-active' : 'chip-default'}" data-cat="">Semua</span>
                ${categories.map(c => `
                    <span class="chip ${selectedCat === String(c.id) ? 'chip-active' : 'chip-default'}" data-cat="${c.id}">${esc(c.name)}</span>
                `).join('')}
            `;

            chipsEl.querySelectorAll('.chip').forEach(chip => {
                chip.addEventListener('click', () => {
                    selectedCat = chip.dataset.cat;
                    updateChips();
                    filterWisata(wisataList, selectedCat);
                });
            });
        }

        updateChips();
    }

    function filterWisata(wisataList, categoryId) {
        const filtered = categoryId
            ? wisataList.filter(w => String(w.category_id) === categoryId)
            : wisataList;
        renderAllWisata(filtered);

        // Also update popular
        const popular = [...filtered].sort((a, b) => b.rating - a.rating).slice(0, 6);
        const popularEl = document.getElementById('popular-wisata');
        if (popularEl) {
            popularEl.innerHTML = popular.length > 0
                ? popular.map(w => WisataCard.card(w)).join('')
                : '<div class="empty-state" style="width:100%;"><p class="empty-state-text">Tidak ada wisata</p></div>';
        }
    }

    function renderAllWisata(list) {
        const el = document.getElementById('all-wisata');
        if (!el) return;
        el.innerHTML = list.length > 0
            ? list.map(w => WisataCard.listCard(w)).join('')
            : `<div class="empty-state">
                <div class="empty-state-icon">😕</div>
                <div class="empty-state-title">Belum ada wisata</div>
                <p class="empty-state-text">Wisata akan segera ditambahkan</p>
               </div>`;
    }

    // ── Banner carousel ──────────────────────────────
    function startBanner() {
        if (bannerInterval) clearInterval(bannerInterval);
        bannerIndex = 0;
        bannerInterval = setInterval(() => {
            bannerIndex = (bannerIndex + 1) % bannerImages.length;
            goToBanner(bannerIndex);
        }, 5000);
    }

    function goToBanner(idx) {
        bannerIndex = idx;
        const slides = document.querySelectorAll('.hero-banner-slide');
        const dots = document.querySelectorAll('.hero-dot');
        slides.forEach((s, i) => s.classList.toggle('active', i === idx));
        dots.forEach((d, i) => d.classList.toggle('active', i === idx));
    }

    function destroy() {
        if (bannerInterval) {
            clearInterval(bannerInterval);
            bannerInterval = null;
        }
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy };
})();

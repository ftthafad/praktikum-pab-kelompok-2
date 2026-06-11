/* ═══════════════════════════════════════════════════════
   Detail Wisata Page
   Mirrors DetailWisataScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const DetailPage = (() => {
    let currentPhotoIndex = 0;
    let wisataData = null;

    async function render(wisataId) {
        const content = document.getElementById('page-content');

        content.innerHTML = `
            <div class="page-wrapper">
                <div class="page-loader"><div class="spinner"></div></div>
            </div>
        `;

        try {
            const [detailRes, reviewsRes] = await Promise.all([
                API.getWisataDetail(wisataId),
                API.getReviews(wisataId).catch(() => ({ data: [] }))
            ]);

            wisataData = detailRes.data;
            const reviews = reviewsRes.data || [];

            if (!wisataData) {
                content.innerHTML = `
                    <div class="page-wrapper">
                        <div class="empty-state" style="padding-top:120px;">
                            <div class="empty-state-icon">😕</div>
                            <div class="empty-state-title">Wisata tidak ditemukan</div>
                            <button class="btn btn-primary" onclick="history.back()" style="margin-top:16px;">
                                <span class="material-icons-round">arrow_back</span> Kembali
                            </button>
                        </div>
                    </div>
                `;
                return;
            }

            // Check bookmark status
            let isBookmarked = false;
            if (Auth.isLoggedIn()) {
                try {
                    const bRes = await API.checkBookmark(wisataId);
                    isBookmarked = bRes.is_bookmarked || false;
                } catch (e) { /* ignore */ }
            }

            const photos = wisataData.photos || [];
            currentPhotoIndex = 0;

            content.innerHTML = `
                <div class="page-enter">
                    <!-- Photo Gallery -->
                    <div class="detail-hero" id="detail-hero">
                        ${photos.length > 0 ? `
                            ${photos.map((p, i) => `
                                <div class="hero-banner-slide ${i === 0 ? 'active' : ''}" data-slide="${i}">
                                    <img src="${p.photo_url}" alt="Photo ${i + 1}" loading="${i === 0 ? 'eager' : 'lazy'}">
                                </div>
                            `).join('')}
                        ` : `
                            <div class="detail-hero-placeholder">
                                <span class="material-icons-round">landscape</span>
                            </div>
                        `}

                        <div class="detail-hero-nav">
                            <button class="detail-hero-btn" onclick="history.back()">
                                <span class="material-icons-round">arrow_back</span>
                            </button>
                            <div style="display:flex;gap:8px;">
                                <button class="detail-hero-btn" id="btn-bookmark" data-bookmarked="${isBookmarked}">
                                    <span class="material-icons-round">${isBookmarked ? 'bookmark' : 'bookmark_border'}</span>
                                </button>
                                <button class="detail-hero-btn" id="btn-share">
                                    <span class="material-icons-round">share</span>
                                </button>
                            </div>
                        </div>

                        ${photos.length > 1 ? `
                            <div class="detail-hero-dots" id="detail-dots">
                                ${photos.map((_, i) => `<div class="detail-hero-dot ${i === 0 ? 'active' : ''}" data-dot="${i}"></div>`).join('')}
                            </div>
                        ` : ''}
                    </div>

                    <!-- Content -->
                    <div class="detail-content">
                        <div class="container">
                            <!-- Main Info Card -->
                            <div class="detail-card">
                                <div class="flex justify-between items-start" style="margin-bottom:12px;">
                                    <div style="flex:1;">
                                        <h1 class="text-h1" style="margin-bottom:4px;">${esc(wisataData.name)}</h1>
                                        <div class="flex items-center gap-xs" style="color:var(--text-secondary);">
                                            <span class="material-icons-round" style="font-size:16px;color:var(--primary);">location_on</span>
                                            <span class="text-body-sm">${esc(wisataData.location)}</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="flex gap-sm" style="margin-bottom:20px;flex-wrap:wrap;">
                                    <span class="info-chip info-chip-star">
                                        <span class="material-icons-round">star</span>
                                        ${Number(wisataData.rating).toFixed(1)}
                                    </span>
                                    <span class="info-chip info-chip-price">
                                        <span class="material-icons-round">confirmation_number</span>
                                        ${esc(wisataData.price)}
                                    </span>
                                    <span class="info-chip info-chip-category">
                                        <span class="material-icons-round">category</span>
                                        ${esc(wisataData.category?.name || '-')}
                                    </span>
                                </div>

                                <div class="divider"></div>

                                <!-- Operating Hours -->
                                <h3 class="detail-section-title">Jam Operasional</h3>
                                <div class="flex items-center gap-xs" style="color:var(--text-secondary);margin-bottom:16px;">
                                    <span class="material-icons-round" style="font-size:16px;color:var(--primary);">access_time</span>
                                    <span class="text-body-sm">${esc(wisataData.opening_hours || 'Tidak tersedia')}</span>
                                </div>

                                <div class="divider"></div>

                                <!-- Description -->
                                <h3 class="detail-section-title">Deskripsi</h3>
                                <p class="text-body" style="color:var(--text-secondary);margin-bottom:16px;white-space:pre-wrap;">${esc(wisataData.description || '-')}</p>

                                <div class="divider"></div>

                                <!-- Map -->
                                <h3 class="detail-section-title">Lokasi</h3>
                                ${wisataData.latitude && wisataData.longitude ? `
                                    <div class="map-container" id="detail-map" style="margin-bottom:12px;"></div>
                                    <a href="https://www.google.com/maps/dir/?api=1&destination=${wisataData.latitude},${wisataData.longitude}"
                                       target="_blank" rel="noopener"
                                       class="btn btn-outline w-full" style="margin-bottom:8px;">
                                        <span class="material-icons-round">directions</span>
                                        Get Direction
                                    </a>
                                ` : `
                                    <p class="text-body-sm" style="color:var(--text-muted);">Lokasi belum tersedia</p>
                                `}
                            </div>

                            <!-- Reviews Section -->
                            <div class="detail-card">
                                <div class="flex justify-between items-center" style="margin-bottom:16px;">
                                    <h3 class="detail-section-title" style="margin:0;">
                                        Ulasan (${reviews.length})
                                    </h3>
                                    <button class="btn btn-sm btn-secondary" id="btn-write-review">
                                        <span class="material-icons-round">edit</span> Tulis Ulasan
                                    </button>
                                </div>

                                <div id="reviews-list" style="display:flex;flex-direction:column;gap:12px;">
                                    ${reviews.length > 0
                                        ? reviews.map(r => renderReview(r)).join('')
                                        : `<p class="text-body" style="color:var(--text-muted);text-align:center;padding:24px 0;">
                                            Belum ada ulasan. Jadilah yang pertama!
                                           </p>`
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            // Init map
            if (wisataData.latitude && wisataData.longitude) {
                initMap(wisataData);
            }

            // Photo dots
            const dotsEl = document.getElementById('detail-dots');
            if (dotsEl) {
                dotsEl.addEventListener('click', (e) => {
                    if (e.target.classList.contains('detail-hero-dot')) {
                        goToPhoto(parseInt(e.target.dataset.dot));
                    }
                });
            }

            // Bookmark
            document.getElementById('btn-bookmark').addEventListener('click', handleBookmark);

            // Share
            document.getElementById('btn-share').addEventListener('click', handleShare);

            // Write review
            document.getElementById('btn-write-review').addEventListener('click', () => {
                if (!Auth.isLoggedIn()) {
                    Modal.show({
                        icon: 'bookmark_border',
                        iconClass: 'modal-icon-info',
                        title: 'Login untuk Menulis Ulasan',
                        body: 'Kamu perlu login terlebih dahulu untuk menulis ulasan.',
                        confirmText: 'Login Sekarang',
                        cancelText: 'Nanti Saja',
                        onConfirm: () => App.navigate('#/login'),
                    });
                    return;
                }
                showReviewForm(wisataId);
            });

        } catch (err) {
            console.error('Detail load failed:', err);
            content.innerHTML = `
                <div class="page-wrapper">
                    <div class="empty-state" style="padding-top:120px;">
                        <div class="empty-state-icon">😕</div>
                        <div class="empty-state-title">Gagal memuat data</div>
                        <p class="empty-state-text">${err.message || 'Periksa koneksi internet'}</p>
                        <button class="btn btn-primary" onclick="history.back()" style="margin-top:16px;">
                            <span class="material-icons-round">arrow_back</span> Kembali
                        </button>
                    </div>
                </div>
            `;
        }
    }

    function renderReview(review) {
        const initial = review.user?.name ? review.user.name.charAt(0).toUpperCase() : '?';
        return `
            <div class="review-card">
                <div class="review-header">
                    <div class="review-user">
                        <div class="review-avatar">${initial}</div>
                        <div>
                            <div class="review-name">${esc(review.user?.name || 'Pengguna')}</div>
                            <div class="review-date">${review.created_at ? review.created_at.substring(0, 10) : ''}</div>
                        </div>
                    </div>
                    ${Rating.display(review.rating)}
                </div>
                ${review.comment ? `<p class="review-comment">${esc(review.comment)}</p>` : ''}
            </div>
        `;
    }

    function showReviewForm(wisataId) {
        const overlay = document.getElementById('modal-overlay');
        overlay.classList.remove('hidden');
        let selectedRating = 0;

        overlay.innerHTML = `
            <div class="modal" style="text-align:left;max-width:480px;">
                <h3 class="modal-title">Tulis Ulasan</h3>
                <p class="modal-body" style="margin-bottom:16px;">Bagikan pengalamanmu mengunjungi tempat ini</p>
                <div class="form-group">
                    <label class="form-label">Rating</label>
                    <div id="review-rating-input"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Komentar (opsional)</label>
                    <textarea class="form-input form-textarea" id="review-comment" placeholder="Tulis komentarmu di sini..." rows="3"></textarea>
                </div>
                <div class="modal-actions" style="justify-content:flex-end;">
                    <button class="btn btn-ghost" id="review-cancel">Batal</button>
                    <button class="btn btn-primary" id="review-submit">
                        <span class="material-icons-round">send</span> Kirim
                    </button>
                </div>
            </div>
        `;

        Rating.input('review-rating-input', (r) => { selectedRating = r; });

        document.getElementById('review-cancel').addEventListener('click', () => Modal.hide());
        overlay.addEventListener('click', (e) => { if (e.target === overlay) Modal.hide(); });

        document.getElementById('review-submit').addEventListener('click', async () => {
            if (selectedRating === 0) {
                Toast.error('Pilih rating terlebih dahulu');
                return;
            }
            const comment = document.getElementById('review-comment').value.trim();
            const submitBtn = document.getElementById('review-submit');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<div class="spinner" style="width:18px;height:18px;border-width:2px;"></div>';

            try {
                await API.submitReview(wisataId, selectedRating, comment || null);
                Modal.hide();
                Toast.success('Ulasan berhasil dikirim!');
                // Reload detail
                render(wisataId);
            } catch (err) {
                Toast.error(err.message || 'Gagal mengirim ulasan');
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<span class="material-icons-round">send</span> Kirim';
            }
        });
    }

    async function handleBookmark() {
        if (!Auth.isLoggedIn()) {
            Modal.show({
                icon: 'bookmark_border',
                iconClass: 'modal-icon-info',
                title: 'Login untuk Menyimpan',
                body: 'Kamu perlu login terlebih dahulu untuk menyimpan wisata favoritmu.',
                confirmText: 'Login Sekarang',
                cancelText: 'Nanti Saja',
                onConfirm: () => App.navigate('#/login'),
            });
            return;
        }

        const btn = document.getElementById('btn-bookmark');
        const isBookmarked = btn.dataset.bookmarked === 'true';

        try {
            await API.toggleBookmark(wisataData.id);
            const newState = !isBookmarked;
            btn.dataset.bookmarked = newState;
            btn.querySelector('.material-icons-round').textContent = newState ? 'bookmark' : 'bookmark_border';
            Toast.success(newState ? 'Disimpan ke bookmark!' : 'Dihapus dari bookmark');
        } catch (err) {
            Toast.error('Gagal mengubah bookmark');
        }
    }

    function handleShare() {
        if (!wisataData) return;
        const shareText = `🌿 ${wisataData.name}\n📍 ${wisataData.location}\n💰 ${wisataData.price}\n⭐ ${wisataData.rating}\n\nTemukan wisata menarik di Jawa Tengah dengan TravelWaka!`;

        if (navigator.share) {
            navigator.share({ title: wisataData.name, text: shareText });
        } else {
            navigator.clipboard.writeText(shareText).then(() => {
                Toast.success('Link berhasil disalin!');
            });
        }
    }

    function initMap(wisata) {
        const mapContainer = document.getElementById('detail-map');
        if (!mapContainer || !wisata.latitude || !wisata.longitude) return;

        try {
            const map = L.map(mapContainer).setView([wisata.latitude, wisata.longitude], 15);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors'
            }).addTo(map);
            L.marker([wisata.latitude, wisata.longitude])
                .addTo(map)
                .bindPopup(`<b>${esc(wisata.name)}</b><br>${esc(wisata.location)}`)
                .openPopup();

            // Fix for map rendering in hidden/animated containers
            setTimeout(() => map.invalidateSize(), 300);
        } catch (e) {
            console.error('Map init error:', e);
        }
    }

    function goToPhoto(idx) {
        currentPhotoIndex = idx;
        const slides = document.querySelectorAll('#detail-hero .hero-banner-slide');
        const dots = document.querySelectorAll('.detail-hero-dot');
        slides.forEach((s, i) => s.classList.toggle('active', i === idx));
        dots.forEach((d, i) => d.classList.toggle('active', i === idx));
    }

    function destroy() {
        wisataData = null;
        currentPhotoIndex = 0;
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy };
})();

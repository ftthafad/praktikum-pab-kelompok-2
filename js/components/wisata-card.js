/* ═══════════════════════════════════════════════════════
   WisataCard Component — Card & List variants
   Mirrors WisataCard.kt from Android
   ═══════════════════════════════════════════════════════ */

const WisataCard = (() => {
    /**
     * Render a grid-style wisata card
     * @param {Object} wisata
     * @returns {string} HTML string
     */
    function card(wisata) {
        const imageUrl = getImageUrl(wisata);
        const imgHtml = imageUrl
            ? `<img src="${imageUrl}" alt="${esc(wisata.name)}" class="wisata-card-img" loading="lazy" onerror="this.parentElement.innerHTML='<div class=\\'wisata-card-img-placeholder\\'><span class=\\'material-icons-round\\'>landscape</span></div>'">`
            : `<div class="wisata-card-img-placeholder"><span class="material-icons-round">landscape</span></div>`;

        return `
            <div class="wisata-card card-interactive" onclick="App.navigate('#/detail/${wisata.id}')" id="wisata-card-${wisata.id}">
                <div style="position:relative;">
                    ${imgHtml}
                    <span class="wisata-card-badge">${esc(wisata.category?.name || 'Wisata')}</span>
                </div>
                <div class="wisata-card-body">
                    <div class="wisata-card-name">${esc(wisata.name)}</div>
                    <div class="wisata-card-location">
                        <span class="material-icons-round">location_on</span>
                        ${esc(wisata.location)}
                    </div>
                    <div class="wisata-card-footer">
                        <span class="wisata-card-rating">
                            <span class="material-icons-round">star</span>
                            ${Number(wisata.rating).toFixed(1)}
                        </span>
                        <span class="wisata-card-price">${esc(wisata.price)}</span>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Render a horizontal list-style wisata card
     * @param {Object} wisata
     * @returns {string} HTML string
     */
    function listCard(wisata) {
        const imageUrl = getImageUrl(wisata);
        const imgHtml = imageUrl
            ? `<img src="${imageUrl}" alt="${esc(wisata.name)}" class="wisata-list-card-img" loading="lazy" onerror="this.style.background='linear-gradient(135deg,#9CD5FF,#7AAACE)';this.src=''">`
            : '';

        return `
            <div class="wisata-list-card card-interactive" onclick="App.navigate('#/detail/${wisata.id}')" id="wisata-list-${wisata.id}">
                ${imgHtml ? imgHtml : `<div class="wisata-list-card-img" style="background:linear-gradient(135deg,#9CD5FF,#7AAACE);display:flex;align-items:center;justify-content:center;"><span class="material-icons-round" style="font-size:32px;color:var(--primary);opacity:0.5;">landscape</span></div>`}
                <div class="wisata-list-card-body">
                    <div class="wisata-card-name">${esc(wisata.name)}</div>
                    <div class="wisata-card-location">
                        <span class="material-icons-round">location_on</span>
                        ${esc(wisata.location)}
                    </div>
                    <div class="wisata-card-footer">
                        <span class="wisata-card-rating">
                            <span class="material-icons-round">star</span>
                            ${Number(wisata.rating).toFixed(1)}
                            <span style="color:var(--text-muted);font-weight:400;margin-left:2px;">(${wisata.review_count || 0})</span>
                        </span>
                        <span class="wisata-card-price">${esc(wisata.price)}</span>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Render skeleton loading cards
     */
    function skeletonCards(count = 4) {
        let html = '';
        for (let i = 0; i < count; i++) {
            html += `
                <div class="wisata-card" style="pointer-events:none;">
                    <div class="skeleton skeleton-img"></div>
                    <div style="padding:14px;">
                        <div class="skeleton skeleton-text" style="width:80%;"></div>
                        <div class="skeleton skeleton-text" style="width:50%;"></div>
                        <div class="skeleton skeleton-text" style="width:60%;margin-top:8px;"></div>
                    </div>
                </div>
            `;
        }
        return html;
    }

    function skeletonList(count = 4) {
        let html = '';
        for (let i = 0; i < count; i++) {
            html += `
                <div class="wisata-list-card" style="pointer-events:none;">
                    <div class="skeleton" style="width:140px;min-width:140px;height:110px;"></div>
                    <div style="padding:14px;flex:1;">
                        <div class="skeleton skeleton-text" style="width:70%;"></div>
                        <div class="skeleton skeleton-text" style="width:45%;"></div>
                        <div class="skeleton skeleton-text" style="width:55%;margin-top:12px;"></div>
                    </div>
                </div>
            `;
        }
        return html;
    }

    // ── Helpers ───────────────────────────────────────
    function getImageUrl(wisata) {
        if (wisata.cover_photo?.photo_url) return wisata.cover_photo.photo_url;
        if (wisata.photos && wisata.photos.length > 0) return wisata.photos[0].photo_url;
        return null;
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { card, listCard, skeletonCards, skeletonList };
})();

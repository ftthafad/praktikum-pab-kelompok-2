/* ═══════════════════════════════════════════════════════
   Form Wisata Page — Add / Edit
   Mirrors FormWisataScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const FormWisataPage = (() => {
    let editWisataId = null;
    let categories = [];

    async function render(wisataId) {
        editWisataId = wisataId || null;
        const isEdit = !!editWisataId;
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn() || Auth.getUserRole() !== 'pengelola') {
            App.navigate('#/profile');
            return;
        }

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <div class="form-page">
                    <div class="form-page-title">
                        <button class="btn btn-icon btn-ghost" onclick="App.navigate('#/pengelola')">
                            <span class="material-icons-round">arrow_back</span>
                        </button>
                        ${isEdit ? 'Edit Wisata' : 'Tambah Wisata Baru'}
                    </div>

                    <div class="detail-card">
                        <form id="wisata-form">
                            <div class="form-group">
                                <label class="form-label" for="fw-name">Nama Wisata *</label>
                                <input type="text" class="form-input" id="fw-name" placeholder="Nama destinasi wisata" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fw-category">Kategori *</label>
                                <select class="form-input form-select" id="fw-category" required>
                                    <option value="">Pilih Kategori</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fw-description">Deskripsi *</label>
                                <textarea class="form-input form-textarea" id="fw-description" placeholder="Deskripsi lengkap wisata..." required rows="5"></textarea>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fw-location">Lokasi *</label>
                                <input type="text" class="form-input" id="fw-location" placeholder="Alamat atau lokasi wisata" required>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label" for="fw-lat">Latitude</label>
                                    <input type="number" step="any" class="form-input" id="fw-lat" placeholder="-6.12345">
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="fw-lng">Longitude</label>
                                    <input type="number" step="any" class="form-input" id="fw-lng" placeholder="110.12345">
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label" for="fw-price">Harga Tiket *</label>
                                    <input type="text" class="form-input" id="fw-price" placeholder="Rp 25.000" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="fw-hours">Jam Operasional</label>
                                    <input type="text" class="form-input" id="fw-hours" placeholder="08:00 - 17:00">
                                </div>
                            </div>

                            ${isEdit ? `
                            <div class="divider" style="margin:24px 0;"></div>
                            <h3 class="detail-section-title">Foto Wisata</h3>

                            <div id="existing-photos" class="photo-preview-grid" style="margin-bottom:12px;"></div>

                            <div class="file-upload-area" id="photo-upload-area">
                                <span class="material-icons-round">cloud_upload</span>
                                <p>Klik untuk upload foto</p>
                                <input type="file" id="fw-photo" accept="image/*" style="display:none;">
                            </div>
                            <div id="upload-status" style="margin-top:8px;"></div>
                            ` : ''}

                            <div id="form-error" class="form-error" style="margin:16px 0;display:none;"></div>

                            <button type="submit" class="btn btn-primary btn-lg w-full" id="form-submit" style="margin-top:20px;">
                                <span class="material-icons-round">${isEdit ? 'save' : 'add'}</span>
                                ${isEdit ? 'Simpan Perubahan' : 'Tambah Wisata'}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        `;

        // Load categories
        try {
            const catRes = await API.getCategories();
            categories = catRes.data || [];
            const select = document.getElementById('fw-category');
            categories.forEach(c => {
                select.innerHTML += `<option value="${c.id}">${esc(c.name)}</option>`;
            });
        } catch (e) { /* ignore */ }

        // If editing, load existing data
        if (isEdit) {
            try {
                const res = await API.getWisataDetail(editWisataId);
                const w = res.data;
                if (w) {
                    document.getElementById('fw-name').value = w.name || '';
                    document.getElementById('fw-category').value = w.category_id || '';
                    document.getElementById('fw-description').value = w.description || '';
                    document.getElementById('fw-location').value = w.location || '';
                    document.getElementById('fw-lat').value = w.latitude || '';
                    document.getElementById('fw-lng').value = w.longitude || '';
                    document.getElementById('fw-price').value = w.price || '';
                    document.getElementById('fw-hours').value = w.opening_hours || '';

                    // Show existing photos
                    renderExistingPhotos(w.photos || []);
                }
            } catch (e) {
                Toast.error('Gagal memuat data wisata');
            }

            // Photo upload
            const uploadArea = document.getElementById('photo-upload-area');
            const photoInput = document.getElementById('fw-photo');

            uploadArea.addEventListener('click', () => photoInput.click());
            photoInput.addEventListener('change', async (e) => {
                const file = e.target.files[0];
                if (!file) return;

                const statusEl = document.getElementById('upload-status');
                statusEl.innerHTML = '<div class="flex items-center gap-sm"><div class="spinner" style="width:16px;height:16px;border-width:2px;"></div> Mengupload...</div>';

                try {
                    await API.uploadFotoWisata(editWisataId, file);
                    Toast.success('Foto berhasil diupload');
                    statusEl.innerHTML = '';
                    // Reload photos
                    const res = await API.getWisataDetail(editWisataId);
                    renderExistingPhotos(res.data?.photos || []);
                } catch (err) {
                    statusEl.innerHTML = `<p class="form-error">${err.message || 'Gagal upload foto'}</p>`;
                }
                photoInput.value = '';
            });
        }

        // Form submit
        document.getElementById('wisata-form').addEventListener('submit', handleSubmit);
    }

    function renderExistingPhotos(photos) {
        const container = document.getElementById('existing-photos');
        if (!container) return;

        container.innerHTML = photos.map(p => `
            <div class="photo-preview-item">
                <img src="${p.photo_url}" alt="Photo" loading="lazy">
                <div class="photo-preview-remove" onclick="FormWisataPage.deletePhoto(${editWisataId}, ${p.id})" title="Hapus foto">
                    <span class="material-icons-round" style="font-size:14px;">close</span>
                </div>
            </div>
        `).join('');
    }

    async function deletePhoto(wisataId, photoId) {
        try {
            await API.deleteFotoWisata(wisataId, photoId);
            Toast.success('Foto dihapus');
            const res = await API.getWisataDetail(wisataId);
            renderExistingPhotos(res.data?.photos || []);
        } catch (err) {
            Toast.error(err.message || 'Gagal menghapus foto');
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        const errorEl = document.getElementById('form-error');
        const submitBtn = document.getElementById('form-submit');

        const data = {
            name: document.getElementById('fw-name').value.trim(),
            category_id: parseInt(document.getElementById('fw-category').value),
            description: document.getElementById('fw-description').value.trim(),
            location: document.getElementById('fw-location').value.trim(),
            latitude: parseFloat(document.getElementById('fw-lat').value) || null,
            longitude: parseFloat(document.getElementById('fw-lng').value) || null,
            price: document.getElementById('fw-price').value.trim(),
            opening_hours: document.getElementById('fw-hours').value.trim(),
        };

        errorEl.style.display = 'none';
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<div class="spinner" style="width:20px;height:20px;border-width:2px;"></div> Menyimpan...';

        try {
            if (editWisataId) {
                await API.updateWisata(editWisataId, data);
                Toast.success('Wisata berhasil diperbarui!');
            } else {
                await API.tambahWisata(data);
                Toast.success('Wisata berhasil ditambahkan!');
            }
            App.navigate('#/pengelola');
        } catch (err) {
            errorEl.textContent = err.message || 'Gagal menyimpan wisata';
            errorEl.style.display = 'block';
            submitBtn.disabled = false;
            const isEdit = !!editWisataId;
            submitBtn.innerHTML = `<span class="material-icons-round">${isEdit ? 'save' : 'add'}</span> ${isEdit ? 'Simpan Perubahan' : 'Tambah Wisata'}`;
        }
    }

    function destroy() {
        editWisataId = null;
    }

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy, deletePhoto };
})();

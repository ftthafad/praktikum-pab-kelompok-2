/* ═══════════════════════════════════════════════════════
   API Service — All backend endpoints
   Mirrors ApiService.kt from Android
   ═══════════════════════════════════════════════════════ */

const API = (() => {
    const BASE_URL = 'http://192.168.128.172:8000/api';

    // ── Core fetch wrapper ────────────────────────────
    async function request(method, path, { body, auth = false, multipart = false } = {}) {
        const headers = {
            'Accept': 'application/json',
            'ngrok-skip-browser-warning': 'true',
        };

        if (auth) {
            headers['Authorization'] = Auth.getBearer();
        }

        const options = { method, headers };

        if (body && !multipart) {
            headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(body);
        } else if (body && multipart) {
            // FormData — don't set Content-Type, let browser handle it
            options.body = body;
        }

        try {
            const res = await fetch(`${BASE_URL}/${path}`, options);
            const data = await res.json();
            if (!res.ok) {
                throw { status: res.status, ...data };
            }
            return data;
        } catch (err) {
            if (err.status) throw err;
            throw { status: 0, message: 'Tidak dapat terhubung ke server. Periksa koneksi internet Anda.' };
        }
    }

    // ── Auth ─────────────────────────────────────────
    function register(name, email, password, passwordConfirmation) {
        return request('POST', 'auth/register', {
            body: { name, email, password, password_confirmation: passwordConfirmation }
        });
    }

    function login(email, password) {
        return request('POST', 'auth/login', {
            body: { email, password }
        });
    }

    function logout() {
        return request('POST', 'auth/logout', { auth: true });
    }

    function me() {
        return request('GET', 'auth/me', { auth: true });
    }

    // ── Wisata ───────────────────────────────────────
    function getWisata() {
        return request('GET', 'wisata');
    }

    function getWisataDetail(id) {
        return request('GET', `wisata/${id}`);
    }

    function getWisataByCategory(categoryId) {
        return request('GET', `wisata/category/${categoryId}`);
    }

    // ── Categories ───────────────────────────────────
    function getCategories() {
        return request('GET', 'categories');
    }

    // ── Bookmarks ────────────────────────────────────
    function getBookmarks() {
        return request('GET', 'bookmarks', { auth: true });
    }

    function toggleBookmark(wisataId) {
        return request('POST', `bookmarks/${wisataId}`, { auth: true });
    }

    function checkBookmark(wisataId) {
        return request('GET', `bookmarks/${wisataId}`, { auth: true });
    }

    // ── Reviews ──────────────────────────────────────
    function getReviews(wisataId) {
        return request('GET', `wisata/${wisataId}/reviews`);
    }

    function checkReview(wisataId) {
        return request('GET', `wisata/${wisataId}/reviews/check`, { auth: true });
    }

    function submitReview(wisataId, rating, comment) {
        return request('POST', `wisata/${wisataId}/reviews`, {
            auth: true,
            body: { rating, comment }
        });
    }

    function deleteReview(wisataId) {
        return request('DELETE', `wisata/${wisataId}/reviews`, { auth: true });
    }

    // ── Pengajuan Pengelola ──────────────────────────
    function submitPengajuan(namaUsaha, deskripsi, alasan) {
        return request('POST', 'pengajuan', {
            auth: true,
            body: { nama_usaha: namaUsaha, deskripsi, alasan }
        });
    }

    function getPengajuanStatus() {
        return request('GET', 'pengajuan/status', { auth: true });
    }

    // ── Super Admin — Approval ───────────────────────
    function getSemuaPengajuan() {
        return request('GET', 'admin/pengajuan', { auth: true });
    }

    function approvePengajuan(id) {
        return request('POST', `admin/pengajuan/${id}/approve`, { auth: true });
    }

    function rejectPengajuan(id, catatanAdmin) {
        return request('POST', `admin/pengajuan/${id}/reject`, {
            auth: true,
            body: { catatan_admin: catatanAdmin }
        });
    }

    // ── Pengelola — CRUD Wisata ──────────────────────
    function getWisataSaya() {
        return request('GET', 'pengelola/wisata', { auth: true });
    }

    function tambahWisata(data) {
        return request('POST', 'pengelola/wisata', {
            auth: true,
            body: data
        });
    }

    function updateWisata(id, data) {
        return request('PUT', `pengelola/wisata/${id}`, {
            auth: true,
            body: data
        });
    }

    function deleteWisata(id) {
        return request('DELETE', `pengelola/wisata/${id}`, { auth: true });
    }

    function uploadFotoWisata(wisataId, file) {
        const formData = new FormData();
        formData.append('photo', file);
        return request('POST', `pengelola/wisata/${wisataId}/photos`, {
            auth: true,
            body: formData,
            multipart: true
        });
    }

    function deleteFotoWisata(wisataId, photoId) {
        return request('DELETE', `pengelola/wisata/${wisataId}/photos/${photoId}`, { auth: true });
    }

    return {
        // Auth
        register, login, logout, me,
        // Wisata
        getWisata, getWisataDetail, getWisataByCategory,
        // Categories
        getCategories,
        // Bookmarks
        getBookmarks, toggleBookmark, checkBookmark,
        // Reviews
        getReviews, checkReview, submitReview, deleteReview,
        // Pengajuan
        submitPengajuan, getPengajuanStatus,
        // Admin
        getSemuaPengajuan, approvePengajuan, rejectPengajuan,
        // Pengelola
        getWisataSaya, tambahWisata, updateWisata, deleteWisata,
        uploadFotoWisata, deleteFotoWisata,
    };
})();

/* ═══════════════════════════════════════════════════════
   API Service — All backend endpoints
   ═══════════════════════════════════════════════════════ */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://192.168.1.18:8000/api';

// ── Core fetch wrapper ────────────────────────────
async function request(method, path, { body, token, multipart = false } = {}) {
  const headers = {
    'Accept': 'application/json',
    'ngrok-skip-browser-warning': 'true',
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const options = { method, headers };

  if (body && !multipart) {
    headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  } else if (body && multipart) {
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
export function register(name, email, password, passwordConfirmation) {
  return request('POST', 'auth/register', {
    body: { name, email, password, password_confirmation: passwordConfirmation }
  });
}

export function login(email, password) {
  return request('POST', 'auth/login', {
    body: { email, password }
  });
}

export function loginWithGoogle(idToken) {
  return request('POST', 'auth/google/android', {
    body: { idToken, id_token: idToken }
  });
}

export function logout(token) {
  return request('POST', 'auth/logout', { token });
}

export function me(token) {
  return request('GET', 'auth/me', { token });
}

// ── Wisata ───────────────────────────────────────
export function getWisata() {
  return request('GET', 'wisata');
}

export function getWisataDetail(id) {
  return request('GET', `wisata/${id}`);
}

export function getWisataByCategory(categoryId) {
  return request('GET', `wisata/category/${categoryId}`);
}

// ── Categories ───────────────────────────────────
export function getCategories() {
  return request('GET', 'categories');
}

// ── Bookmarks ────────────────────────────────────
export function getBookmarks(token) {
  return request('GET', 'bookmarks', { token });
}

export function toggleBookmark(wisataId, token) {
  return request('POST', `bookmarks/${wisataId}`, { token });
}

export function checkBookmark(wisataId, token) {
  return request('GET', `bookmarks/${wisataId}`, { token });
}

// ── Reviews ──────────────────────────────────────
export function getReviews(wisataId) {
  return request('GET', `wisata/${wisataId}/reviews`);
}

export function checkReview(wisataId, token) {
  return request('GET', `wisata/${wisataId}/reviews/check`, { token });
}

export function submitReview(wisataId, rating, comment, token) {
  return request('POST', `wisata/${wisataId}/reviews`, {
    token,
    body: { rating, comment }
  });
}

export function deleteReview(wisataId, token) {
  return request('DELETE', `wisata/${wisataId}/reviews`, { token });
}

// ── Pengajuan Pengelola ──────────────────────────
export function submitPengajuan(namaUsaha, deskripsi, alasan, token) {
  return request('POST', 'pengajuan', {
    token,
    body: { nama_usaha: namaUsaha, deskripsi, alasan }
  });
}

export function getPengajuanStatus(token) {
  return request('GET', 'pengajuan/status', { token });
}

// ── Super Admin — Approval ───────────────────────
export function getSemuaPengajuan(token) {
  return request('GET', 'admin/pengajuan', { token });
}

export function approvePengajuan(id, token) {
  return request('POST', `admin/pengajuan/${id}/approve`, { token });
}

export function rejectPengajuan(id, catatanAdmin, token) {
  return request('POST', `admin/pengajuan/${id}/reject`, {
    token,
    body: { catatan_admin: catatanAdmin }
  });
}

// ── Pengelola — CRUD Wisata ──────────────────────
export function getWisataSaya(token) {
  return request('GET', 'pengelola/wisata', { token });
}

export function tambahWisata(data, token) {
  return request('POST', 'pengelola/wisata', {
    token,
    body: data
  });
}

export function updateWisata(id, data, token) {
  return request('PUT', `pengelola/wisata/${id}`, {
    token,
    body: data
  });
}

export function deleteWisata(id, token) {
  return request('DELETE', `pengelola/wisata/${id}`, { token });
}

export function uploadFotoWisata(wisataId, file, token) {
  const formData = new FormData();
  formData.append('photo', file);
  return request('POST', `pengelola/wisata/${wisataId}/photos`, {
    token,
    body: formData,
    multipart: true
  });
}

export function deleteFotoWisata(wisataId, photoId, token) {
  return request('DELETE', `pengelola/wisata/${wisataId}/photos/${photoId}`, { token });
}

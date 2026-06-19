# 🌍 TravelWaka

**Jelajahi Wisata Jawa Tengah** — Temukan destinasi wisata terbaik di Jawa Tengah. Jelajahi alam, budaya, kuliner, dan petualangan seru bersama TravelWaka.

> Proyek Pemrograman Aplikasi Bergerak, Pemrograman Web, Rekayasa Perangkat Lunak — Kelompok 2

---

## ✨ Fitur

- 🔐 **Autentikasi** — Login & Register dengan email/password atau Google Sign-In
- 🏠 **Beranda** — Tampilan utama dengan rekomendasi wisata
- 🔍 **Eksplorasi** — Jelajahi wisata berdasarkan kategori
- 📍 **Detail Wisata** — Informasi lengkap, foto, lokasi peta (Leaflet), dan ulasan
- ⭐ **Ulasan & Rating** — Berikan rating dan komentar pada wisata
- 🔖 **Bookmark** — Simpan wisata favorit
- 📋 **Pengajuan Pengelola** — Ajukan diri sebagai pengelola wisata
- 🏢 **Dashboard Pengelola** — Kelola wisata (CRUD) dan upload foto
- 🛡️ **Dashboard Admin** — Approval/reject pengajuan pengelola
- 🔔 **Notifikasi** — Status pengajuan pengelola
- 👤 **Profil** — Informasi akun, statistik, dan pengaturan

---

## 🛠️ Tech Stack

| Teknologi | Keterangan |
|---|---|
| [React](https://react.dev/) | Library UI |
| [Vite](https://vite.dev/) | Build tool & dev server |
| [React Router](https://reactrouter.com/) | Routing (HashRouter) |
| [Leaflet](https://leafletjs.com/) + [React Leaflet](https://react-leaflet.js.org/) | Peta interaktif |
| [Google Identity Services](https://developers.google.com/identity) | Google Sign-In |
| [Material Icons](https://fonts.google.com/icons) | Ikon UI |
| [Inter](https://fonts.google.com/specimen/Inter) | Font utama |

---

## 📁 Struktur Proyek

```
website/
├── public/                 # Aset statis
├── src/
│   ├── assets/             # Gambar & aset
│   ├── components/         # Komponen reusable
│   │   ├── Navbar.jsx
│   │   ├── RatingDisplay.jsx
│   │   ├── RatingInput.jsx
│   │   └── WisataCard.jsx
│   ├── contexts/           # React Context (state management)
│   │   ├── AuthContext.jsx
│   │   ├── ModalContext.jsx
│   │   └── ToastContext.jsx
│   ├── pages/              # Halaman aplikasi
│   │   ├── AdminDashboardPage.jsx
│   │   ├── BookmarkPage.jsx
│   │   ├── DetailPage.jsx
│   │   ├── ExplorePage.jsx
│   │   ├── FormWisataPage.jsx
│   │   ├── HomePage.jsx
│   │   ├── LoginPage.jsx
│   │   ├── NotifikasiPage.jsx
│   │   ├── OnboardingPage.jsx
│   │   ├── PengajuanPage.jsx
│   │   ├── PengelolaPage.jsx
│   │   ├── ProfilePage.jsx
│   │   ├── RegisterPage.jsx
│   │   └── ReviewPage.jsx
│   ├── services/
│   │   └── api.js          # API service layer
│   ├── App.jsx             # Root component & routing
│   ├── index.css           # Global styles
│   └── main.jsx            # Entry point
├── .env.example            # Contoh environment variables
├── index.html              # HTML template
├── package.json
└── vite.config.js
```

---

## 🚀 Cara Menjalankan

### Prasyarat

- [Node.js](https://nodejs.org/) (v18 atau lebih baru)
- npm (sudah termasuk bersama Node.js)
- Backend API yang sudah berjalan

### Instalasi

1. **Clone repository**

   ```bash
   git clone https://github.com/username/praktikum-pab-kelompok-2.git
   cd praktikum-pab-kelompok-2
   ```

2. **Install dependencies**

   ```bash
   npm install
   ```

3. **Konfigurasi environment variables**

   Salin file `.env.example` menjadi `.env`, lalu isi dengan nilai yang sesuai:

   ```bash
   cp .env.example .env
   ```

   Isi file `.env`:

   ```env
   VITE_API_BASE_URL=http://localhost:8000/api/
   VITE_GOOGLE_CLIENT_ID=your_google_client_id_here
   ```

   | Variable | Keterangan |
   |---|---|
   | `VITE_API_BASE_URL` | URL base endpoint API backend |
   | `VITE_GOOGLE_CLIENT_ID` | Client ID dari Google Cloud Console untuk fitur Google Sign-In |

4. **Jalankan development server**

   ```bash
   npm run dev
   ```

   Aplikasi akan berjalan di `http://localhost:5173`.

---

## 📦 Scripts

| Script | Perintah | Keterangan |
|---|---|---|
| Dev server | `npm run dev` | Menjalankan development server |
| Build | `npm run build` | Build untuk production |
| Preview | `npm run preview` | Preview hasil build |
| Lint | `npm run lint` | Cek kualitas kode dengan ESLint |

---

## 🔑 Konfigurasi Google Sign-In

1. Buka [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Buat atau pilih project
3. Buat **OAuth 2.0 Client ID** (tipe: Web application)
4. Tambahkan **Authorized JavaScript Origins**:
   - `http://localhost:5173` (untuk development)
   - URL production (jika sudah deploy)
5. Salin **Client ID** dan masukkan ke file `.env`

---

## 👥 Tim Pengembang

**Kelompok 2** — Pemrograman Aplikasi Bergerak, Pemrograman Web, Rekayasa Perangkat Lunak

---

## 📄 Lisensi

© 2026 Kelompok 2. All Rights Reserved.

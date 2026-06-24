# 📋 Code Review Dosen — Proyek TravelWaka

> Review sebagai **dosen mata kuliah Pemrograman Aplikasi Bergerak (PAB)** terhadap proyek praktikum kelompok 2.
> Proyek ini terdiri dari **Android App (Kotlin/Compose)** + **Website (React/Vite)**.

---

## 📊 Ringkasan Penilaian

| Aspek | Nilai | Keterangan |
|-------|-------|------------|
| Arsitektur & Struktur | B | MVVM + Repository + DI sudah ada, tapi ada duplikasi & inkonsistensi |
| Keamanan | D | Ada kebocoran secret kritis di source code |
| Testing | E | **Tidak ada test sama sekali** (hanya template default) |
| Code Quality | C+ | Banyak kode duplikat, naming inkonsisten |
| Error Handling | C | Pola ada tapi banyak `catch` yang di-ignore |
| UI/UX Code | B+ | Compose sudah baik, shimmer loading bagus |
| Website Code | B | Cukup rapi, tapi ada masalah serupa |
| **Total Estimasi** | **C+** | **Banyak perbaikan fundamental diperlukan** |

---

## 🔴 MASALAH KRITIS (Akan Sangat Mengurangi Nilai)

### 1. 🔑 HARDCODED SECRET — Google Client ID di Source Code

> [!CAUTION]
> **Ini adalah kesalahan keamanan FATAL.**

Di [LoginScreen.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/ui/screens/auth/LoginScreen.kt#L234):
```kotlin
.setServerClientId("1005594778345-eu52f1263rk8kpt0g85jkbk8bl4g6sc3.apps.googleusercontent.com")
```

**Masalah:**
- Google OAuth Client ID di-hardcode langsung di UI layer
- Seharusnya disimpan di `BuildConfig`, `local.properties`, atau `secrets.gradle`
- Jika repository publik, credential ini bisa disalahgunakan

**Saran perbaikan:**
```kotlin
// Di build.gradle.kts:
buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProperties.getProperty("GOOGLE_CLIENT_ID")}\"")

// Di LoginScreen.kt:
.setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
```

---

### 2. 📁 File `.env` Tidak di-Gitignore dan Di-Commit

> [!CAUTION]
> File [.env](file:///Users/user/github/TravelWaka/main/.env) berisi konfigurasi sensitif Laravel (database, mail, AWS keys), tapi **TIDAK** ada di `.gitignore`.

[.gitignore](file:///Users/user/github/TravelWaka/main/.gitignore) tidak menyebutkan `.env` sama sekali. Ini berarti file `.env` bisa ter-commit ke Git repository publik.

---

### 3. ❌ TIDAK ADA UNIT TEST SAMA SEKALI

> [!WARNING]
> **Ini poin pengurangan nilai terbesar untuk mata kuliah praktikum.**

Folder test hanya berisi template default:
- `app/src/test/java/com/travelwaka/app/ExampleUnitTest.kt`
- `app/src/androidTest/java/com/travelwaka/app/ExampleInstrumentedTest.kt`

Tidak ada satupun test yang ditulis untuk:
- ViewModel (logic bisnis)
- Repository
- API Service
- Utility functions (ImageCompressor, dll)

**Seharusnya minimal ada:**
- Unit test untuk setiap ViewModel
- Unit test untuk validasi di AuthViewModel (login/register validation)
- Integration test untuk Repository

---

## 🟠 MASALAH ARSITEKTUR & DESAIN (Mengurangi Nilai Signifikan)

### 4. `WisataRepository` Terlalu Gemuk (God Interface)

[WisataRepository.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/data/repository/WisataRepository.kt) memiliki **18 method** yang mencakup:
- Wisata CRUD
- Bookmark
- Review
- Pengajuan pengelola
- Admin approval
- Upload foto

**Masalah:** Ini melanggar **Single Responsibility Principle (SRP)**. Satu interface bertanggung jawab untuk SEMUA domain bisnis.

**Seharusnya dipisah:**
```
WisataRepository         → getWisata, getWisataDetail, getCategories
BookmarkRepository       → getBookmarks, toggleBookmark, checkBookmark
ReviewRepository         → getReviews, submitReview, deleteReview, checkReview
PengajuanRepository      → submitPengajuan, getPengajuanStatus
AdminRepository          → getSemuaPengajuan, approvePengajuan, rejectPengajuan
PengelolaRepository      → getWisataSaya, tambahWisata, updateWisata, deleteWisata, uploadFoto, deleteFoto
```

---

### 5. `WisataViewModel` Tidak Digunakan (Dead Code?)

[WisataViewModel.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/viewmodel/WisataViewModel.kt) memiliki fungsi yang **hampir identik** dengan `HomeViewModel` dan `ExploreViewModel`:
- `getWisata()` → duplikat dari `HomeViewModel.loadAllWisata()`
- `getWisataDetail()` → duplikat dari `DetailWisataViewModel.loadDetail()`
- `getWisataByCategory()` → duplikat dari `HomeViewModel.loadWisataByCategory()`

**Ini dead code yang membingungkan.** Jika tidak dipakai, hapus.

---

### 6. Duplikasi Logika Bookmark di 3 ViewModel Berbeda

Logika `loadBookmarks()` dan `toggleBookmark()` di-copy-paste hampir identik di:
- [HomeViewModel.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/viewmodel/HomeViewModel.kt#L63-L101)
- [ExploreViewModel.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/viewmodel/ExploreViewModel.kt#L85-L123)
- [BookmarkViewModel.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/viewmodel/BookmarkViewModel.kt#L76-L99)

**Ini melanggar prinsip DRY (Don't Repeat Yourself).** Seharusnya ada shared bookmark state management, misalnya:
- Sebuah `BookmarkManager` yang di-inject ke semua ViewModel
- Atau menggunakan shared `StateFlow` via Hilt

---

### 7. `AuthRepositoryImpl` Hanyalah Thin Wrapper (Tidak Ada Value)

[AuthRepositoryImpl.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/data/repository/AuthRepositoryImpl.kt) hanya forward call ke ApiService tanpa tambahan logic:

```kotlin
override suspend fun login(request: LoginRequest): AuthResponse = apiService.login(request)
```

Begitu juga [WisataRepositoryImpl.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/data/repository/WisataRepositoryImpl.kt). Tidak ada:
- Caching
- Error mapping/transformation
- Offline support
- Data mapping dari network model ke domain model

Repository layer yang hanya proxy ke API Service tidak memberikan value. **Seharusnya** repository layer menjadi boundary antara data source dan domain, melakukan mapping dan caching.

---

### 8. Tidak Ada Domain/Use Case Layer

Arsitektur langsung dari `ViewModel → Repository → ApiService`. Tidak ada Use Case / Interactor layer.

Untuk proyek akademik, **Clean Architecture** seharusnya memiliki:
```
UI (Screen) → ViewModel → UseCase → Repository → DataSource (API/DB)
```

---

## 🟡 MASALAH CODE QUALITY (Mengurangi Nilai Moderat)

### 9. `runBlocking` di AuthInterceptor

[AuthInterceptor.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/../../../network/AuthInterceptor.kt#L17):
```kotlin
val token = runBlocking {
    tokenDataStore.token.first()
}
```

> [!WARNING]
> `runBlocking` di interceptor OkHttp bisa menyebabkan **deadlock** pada beberapa kasus, terutama jika DataStore dan network call berada di thread yang sama. Gunakan pendekatan `TokenAuthenticator` atau cache token di memory.

---

### 10. Debug Log yang Masih Ada di Production Code

[TokenDataStore.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/datastore/TokenDataStore.kt) berisi debug log yang seharusnya tidak ada di production:

```kotlin
// Line 27 — Log token yang disimpan (SENSITIF!)
android.util.Log.d("DataStore", "Menyimpan: token=$token, role=$role")

// Line 50 — Log token setiap kali dibaca
android.util.Log.d("DataStore", "token dibaca: $t")

// Line 72 — Log stack trace setiap logout
android.util.Log.d("DataStore", Thread.currentThread().stackTrace.joinToString("\n"))
```

Dan di [AppNavigation.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/ui/navigation/AppNavigation.kt#L80):
```kotlin
android.util.Log.d("AppNav", "token: $token, role: $userRole, onboarding: $hasSeenOnboarding")
```

**Masalah:**
- Token user ditulis ke logcat → **keamanan buruk**
- Stack trace di-log setiap logout → **noise dan performa**
- Gunakan Timber atau `BuildConfig.DEBUG` check

---

### 11. Ngrok Header di Production Code

[NetworkModule.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/NetworkModule.kt#L29):
```kotlin
.addHeader("ngrok-skip-browser-warning", "true")
```
[api.js](file:///Users/user/github/TravelWaka/main/website/src/services/api.js#L11):
```javascript
'ngrok-skip-browser-warning': 'true',
```

Ini menunjukkan development menggunakan ngrok tunnel. Header ini seharusnya **hanya ada di build variant debug**, bukan di production code.

---

### 12. Naming Convention Tidak Konsisten (Bahasa Campur)

| File | Masalah |
|------|---------|
| [Pengajuanresponse.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/Pengajuanresponse.kt) | Nama file huruf kecil "response", seharusnya `PengajuanResponse.kt` |
| [Pengelolaresponse.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/Pengelolaresponse.kt) | Sama, seharusnya `PengelolaResponse.kt` |
| [Reviewresponse.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/Reviewresponse.kt) | Sama, seharusnya `ReviewResponse.kt` |
| Method `tambahWisata()` | Bahasa Indonesia, tapi `deleteWisata()` bahasa Inggris |
| Method `getWisataSaya()` | Campur, seharusnya konsisten satu bahasa |

**Gunakan satu bahasa konsisten** — idealnya English, atau jika pakai Bahasa Indonesia maka semua harus Indonesia.

---

### 13. Model Data Menggunakan `snake_case` Tanpa `@SerializedName` (Inkonsisten)

[Wisata.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/Wisata.kt):
```kotlin
data class Wisata(
    val user_id: Int,          // ← snake_case tanpa @SerializedName
    val category_id: Int,      // ← snake_case
    val opening_hours: String?, // ← snake_case
    val cover_photo: Photo?,   // ← snake_case
    val created_at: String?    // ← snake_case
)
```

Tapi di [BookmarkResponse.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/network/model/BookmarkResponse.kt):
```kotlin
data class BookmarkItem(
    @SerializedName("user_id") val userId: Int,    // ← camelCase + @SerializedName ✅
    @SerializedName("wisata_id") val wisataId: Int, // ← camelCase + @SerializedName ✅
)
```

**Masalah:** Dua pola berbeda untuk hal yang sama. Seharusnya konsisten — idealnya selalu gunakan `camelCase` di Kotlin + `@SerializedName` untuk mapping ke API snake_case.

---

### 14. Duplicate Dependency Declarations di `build.gradle.kts`

[build.gradle.kts](file:///Users/user/github/TravelWaka/main/app/build.gradle.kts) mendeklarasi dependency **dua kali**:

```kotlin
// Via version catalog (line 61-68):
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.ui)

// Dan langsung hardcoded (line 77-91):
implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.ui:ui")
```

Ini menyebabkan **konflik versi** dan dependency resolution yang tidak terprediksi.

---

### 15. Error Handling yang Di-Ignore

Banyak catch block hanya `// ignore`:

```kotlin
// HomeViewModel.kt:77
} catch (e: Exception) {
    // ignore
}

// AuthViewModel.kt:69
} catch (e: Exception) {
    // ignore
}

// ExploreViewModel.kt:99
} catch (e: Exception) {
    // ignore
}
```

Error yang di-ignore secara diam-diam membuat debugging sangat sulit dan menyembunyikan masalah.

---

### 16. `AppNavigation` Membuat Instance `TokenDataStore` Baru

[AppNavigation.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/ui/navigation/AppNavigation.kt#L74):
```kotlin
val tokenDataStore = remember { TokenDataStore(context) }
```

Padahal `TokenDataStore` sudah di-provide melalui **Hilt DI** di [DataStoreModule.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/di/DataStoreModule.kt). Membuat instance baru di sini berarti:
- Ada **2 instance berbeda** dari TokenDataStore di aplikasi
- Data mungkin tidak sinkron antara Navigation dan ViewModel
- Melanggar tujuan dependency injection

---

### 17. `WisataItem` Dead Code di WisataCard.kt

[WisataCard.kt](file:///Users/user/github/TravelWaka/main/app/src/main/java/com/travelwaka/app/ui/components/WisataCard.kt#L24-L34):
```kotlin
// ✅ Tetap ada untuk backward compatibility (dummy data)
data class WisataItem(...)
```

Komentar sendiri mengatakan "backward compatibility" tapi ini **dead code**. Tidak pernah digunakan di codebase. Hapus.

---

### 18. Website: Fallback BASE_URL ke IP Lokal

[api.js](file:///Users/user/github/TravelWaka/main/website/src/services/api.js#L5):
```javascript
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://192.168.1.18:8000/api';
```

IP `192.168.1.18` hanya valid di jaringan lokal developer. Di production, ini akan gagal total.

---

### 19. Website: CSS Monolith — 2.918 Baris dalam 1 File

[index.css](file:///Users/user/github/TravelWaka/main/website/src/index.css) memiliki **2.918 baris** CSS dalam satu file. Ini sangat sulit di-maintain.

**Seharusnya dipisah** menjadi:
```
styles/
├── variables.css
├── base.css
├── navbar.css
├── cards.css
├── forms.css
├── pages/
│   ├── home.css
│   ├── detail.css
│   └── ...
└── responsive.css
```

---

### 20. Website: Tidak Ada Protected Route

Semua route di [App.jsx](file:///Users/user/github/TravelWaka/main/website/src/App.jsx) terbuka. Tidak ada guard untuk halaman yang membutuhkan login:
- `/bookmark` — seharusnya perlu login
- `/profile` — seharusnya perlu login
- `/pengajuan` — seharusnya perlu login
- `/admin` — seharusnya perlu login + role check
- `/pengelola` — seharusnya perlu login + role check

---

### 21. isMinifyEnabled = false di Release Build

[build.gradle.kts](file:///Users/user/github/TravelWaka/main/app/build.gradle.kts#L43):
```kotlin
release {
    isMinifyEnabled = false
}
```

ProGuard/R8 tidak diaktifkan di release build, sehingga:
- APK size lebih besar
- Code tidak di-obfuscate (mudah di-reverse engineer)
- Tidak ada tree-shaking

---

## 🟢 HAL YANG SUDAH BAIK (Poin Plus)

| Aspek | Catatan |
|-------|---------|
| ✅ MVVM Pattern | Penggunaan ViewModel + StateFlow sudah benar |
| ✅ Hilt DI | Dependency Injection sudah diimplementasi dengan benar |
| ✅ Repository Pattern | Meskipun terlalu gemuk, pattern-nya sudah ada |
| ✅ Compose UI | Penggunaan Jetpack Compose sudah modern dan rapi |
| ✅ Shimmer Loading | Implementasi loading skeleton bagus di HomeScreen |
| ✅ Navigation 3 | Penggunaan Navigation 3 library yang baru |
| ✅ Auth Flow | Flow login/register/Google cukup lengkap |
| ✅ Theme System | Color palette dan Typography sudah terstruktur |
| ✅ Image Compression | Utility ImageCompressor sudah ada untuk upload |
| ✅ Website Design System | CSS variables dan design tokens sudah terstruktur rapi |
| ✅ Website API Wrapper | `request()` function cukup clean dan reusable |
| ✅ Context Pattern | AuthContext dan ToastContext di website sudah tepat |
| ✅ Role-Based Navigation | Sudah ada routing berdasarkan role user |

---

## 📝 Rekomendasi Prioritas Perbaikan

### Prioritas 1 — Wajib diperbaiki (Keamanan):
1. Pindahkan Google Client ID ke `BuildConfig` / `local.properties`
2. Tambahkan `.env` ke `.gitignore`
3. Hapus semua debug log yang menampilkan token
4. Hapus ngrok header dari production code

### Prioritas 2 — Sangat penting (Nilai):
5. **Tulis unit test** — minimal untuk ViewModel dan validasi
6. Pecah `WisataRepository` menjadi beberapa repository terpisah
7. Hapus dead code (`WisataViewModel`, `WisataItem`)
8. Fix duplikasi dependency di `build.gradle.kts`

### Prioritas 3 — Penting (Code Quality):
9. Konsistensi naming (satu bahasa, PascalCase file, camelCase fields)
10. Extract logika bookmark ke shared manager
11. Tambahkan protected routes di website
12. Pecah monolith CSS ke beberapa file
13. Aktifkan ProGuard di release build

---

> **Catatan Dosen:** Secara keseluruhan, proyek ini menunjukkan pemahaman dasar arsitektur yang baik (MVVM, DI, Repository) dan fitur yang cukup lengkap. Namun, aspek **keamanan, testing, dan code quality** masih sangat perlu diperbaiki. Di dunia nyata, hardcoded credentials dan zero test coverage bisa menjadi alasan project ditolak saat code review.

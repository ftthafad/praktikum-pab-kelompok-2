package com.travelwaka.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.BookmarkItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {
    private val tokenDataStore = TokenDataStore.getInstance(context)

    // --- State: daftar bookmark yang dimiliki user ---
    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    // --- State: apakah user sudah login (token tersedia) ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // --- State: loading & error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inisialisasi: cek token lalu load bookmark jika sudah login
    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = tokenDataStore.token.first()
                if (token.isNullOrEmpty()) {
                    // Belum login, tampilkan state kosong
                    _isLoggedIn.value = false
                    _bookmarks.value = emptyList()
                } else {
                    _isLoggedIn.value = true
                    val response = apiService.getBookmarks("Bearer $token")
                    if (response.status) {
                        _bookmarks.value = response.data
                    } else {
                        _errorMessage.value = "Gagal memuat bookmark"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internetmu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dipanggil setelah user toggle bookmark di DetailWisataScreen
    // agar daftar bookmark di sini ikut ter-refresh
    fun refresh() {
        loadBookmarks()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
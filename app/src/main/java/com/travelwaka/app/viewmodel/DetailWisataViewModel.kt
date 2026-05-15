package com.travelwaka.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.ApiClient
import com.travelwaka.app.network.model.Wisata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailWisataViewModel(private val context: Context) : ViewModel() {

    private val apiService = ApiClient.apiService
    private val tokenDataStore = TokenDataStore.getInstance(context)

    // --- State: data detail wisata ---
    private val _wisata = MutableStateFlow<Wisata?>(null)
    val wisata: StateFlow<Wisata?> = _wisata.asStateFlow()

    // --- State: status bookmark wisata ini ---
    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    // --- State: pesan snackbar setelah toggle bookmark ---
    private val _bookmarkMessage = MutableStateFlow<String?>(null)
    val bookmarkMessage: StateFlow<String?> = _bookmarkMessage.asStateFlow()

    // --- State: apakah user sudah login ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // --- State: loading & error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Dipanggil dari screen saat wisataId tersedia
    fun loadDetail(wisataId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil detail wisata
                val response = apiService.getWisataDetail(wisataId)
                if (response.status) {
                    _wisata.value = response.data
                } else {
                    _errorMessage.value = response.message
                }

                // Cek token & status bookmark sekaligus
                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    _isLoggedIn.value = true
                    val bookmarkResponse = apiService.checkBookmark("Bearer $token", wisataId)
                    _isBookmarked.value = bookmarkResponse.isBookmarked
                } else {
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data wisata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dipanggil saat user tap icon bookmark
    fun toggleBookmark(wisataId: Int) {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.token.first()
                if (token.isNullOrEmpty()) return@launch
                val response = apiService.toggleBookmark("Bearer $token", wisataId)
                if (response.status) {
                    _isBookmarked.value = response.isBookmarked
                    _bookmarkMessage.value = response.message
                }
            } catch (e: Exception) {
                _bookmarkMessage.value = "Gagal mengubah bookmark"
            }
        }
    }

    fun clearBookmarkMessage() {
        _bookmarkMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.data.BookmarkManager
import com.travelwaka.app.network.model.Wisata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailWisataViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val wisataRepository: WisataRepository,
    private val bookmarkManager: BookmarkManager
) : ViewModel() {

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

    init {
        // Amati perubahan bookmark IDs secara reaktif
        viewModelScope.launch {
            bookmarkManager.bookmarkedIds.collect { ids ->
                val currentWisataId = _wisata.value?.id
                if (currentWisataId != null) {
                    _isBookmarked.value = ids.contains(currentWisataId)
                }
            }
        }
    }

    // Dipanggil dari screen saat wisataId tersedia
    fun loadDetail(wisataId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil detail wisata
                val response = wisataRepository.getWisataDetail(wisataId)
                if (response.status) {
                    _wisata.value = response.data
                } else {
                    _errorMessage.value = response.message
                }

                // Cek token & status bookmark sekaligus
                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    _isLoggedIn.value = true
                    _isBookmarked.value = bookmarkManager.bookmarkedIds.value.contains(wisataId)
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
            val result = bookmarkManager.toggleBookmark(wisataId)
            if (result.isSuccess) {
                val isBookmarkedResult = result.getOrNull() == true
                _isBookmarked.value = isBookmarkedResult
                _bookmarkMessage.value = if (isBookmarkedResult) "Wisata berhasil disimpan" else "Wisata dihapus dari bookmark"
            } else {
                _bookmarkMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengubah bookmark"
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
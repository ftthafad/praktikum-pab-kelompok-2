package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.data.repository.BookmarkRepository
import com.travelwaka.app.data.BookmarkManager
import com.travelwaka.app.network.model.BookmarkItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val bookmarkRepository: BookmarkRepository,
    private val bookmarkManager: BookmarkManager
) : ViewModel() {

    // --- State: daftar bookmark yang dimiliki user ---
    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    // --- State: apakah user sudah login ---
    val isLoggedIn: StateFlow<Boolean> = tokenDataStore.token
        .map { !it.isNullOrEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // --- State: loading & error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inisialisasi: amati perubahan bookmark IDs secara reaktif dan muat ulang list
    init {
        viewModelScope.launch {
            bookmarkManager.bookmarkedIds.collect { ids ->
                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    fetchBookmarks()
                } else {
                    _bookmarks.value = emptyList()
                }
            }
        }
    }

    private fun fetchBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = bookmarkRepository.getBookmarks()
                if (response.status) {
                    _bookmarks.value = response.data
                } else {
                    _errorMessage.value = "Gagal memuat bookmark"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internetmu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            val token = tokenDataStore.token.first()
            if (!token.isNullOrEmpty()) {
                fetchBookmarks()
            } else {
                _bookmarks.value = emptyList()
            }
        }
    }

    fun toggleBookmark(wisataId: Int) {
        viewModelScope.launch {
            val result = bookmarkManager.toggleBookmark(wisataId)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengubah bookmark"
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
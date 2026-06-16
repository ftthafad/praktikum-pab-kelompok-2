package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.network.model.Category
import com.travelwaka.app.network.model.Wisata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val wisataRepository: WisataRepository
) : ViewModel() {

    // --- State: daftar bookmark yang dimiliki user ---
    private val _bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedIds: StateFlow<Set<Int>> = _bookmarkedIds.asStateFlow()

    // --- State: apakah user sudah login ---
    val isLoggedIn: StateFlow<Boolean> = tokenDataStore.token
        .map { !it.isNullOrEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // --- State: daftar wisata yang ditampilkan (bisa semua / by kategori) ---
    private val _wisataList = MutableStateFlow<List<Wisata>>(emptyList())
    val wisataList: StateFlow<List<Wisata>> = _wisataList.asStateFlow()

    // --- State: daftar kategori untuk chip filter ---
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // --- State: kategori yang sedang dipilih (null = semua) ---
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // --- State: loading & error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inisialisasi: load data pertama kali ViewModel dibuat
    init {
        loadInitialData()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    val response = wisataRepository.getBookmarks()
                    if (response.status) {
                        val ids = response.data.map { it.wisata.id }.toSet()
                        _bookmarkedIds.value = ids
                    }
                } else {
                    _bookmarkedIds.value = emptySet()
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun toggleBookmark(wisataId: Int) {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.token.first()
                if (token.isNullOrEmpty()) return@launch
                val response = wisataRepository.toggleBookmark(wisataId)
                if (response.status) {
                    val currentIds = _bookmarkedIds.value.toMutableSet()
                    if (response.isBookmarked) {
                        currentIds.add(wisataId)
                    } else {
                        currentIds.remove(wisataId)
                    }
                    _bookmarkedIds.value = currentIds
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengubah bookmark"
            }
        }
    }

    // Load wisata & kategori sekaligus saat pertama kali
    private fun loadInitialData() {
        loadBookmarks()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Jalankan keduanya, kategori tidak perlu tampilkan loading terpisah
                val wisataResponse = wisataRepository.getWisata()
                if (wisataResponse.status) {
                    _wisataList.value = wisataResponse.data ?: emptyList()
                } else {
                    _errorMessage.value = wisataResponse.message
                }

                val categoryResponse = wisataRepository.getCategories()
                if (categoryResponse.status) {
                    _categories.value = categoryResponse.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internetmu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dipanggil saat user mengetuk chip kategori
    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
        if (categoryId == null) {
            loadAllWisata()
        } else {
            loadWisataByCategory(categoryId)
        }
    }

    private fun loadAllWisata() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = wisataRepository.getWisata()
                if (response.status) {
                    _wisataList.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data wisata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadWisataByCategory(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = wisataRepository.getWisataByCategory(categoryId)
                if (response.status) {
                    _wisataList.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data wisata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
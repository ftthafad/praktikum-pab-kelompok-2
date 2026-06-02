package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.Category
import com.travelwaka.app.network.model.Wisata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // --- State: semua wisata dari API (sebelum difilter search) ---
    private val _wisataList = MutableStateFlow<List<Wisata>>(emptyList())

    // --- State: kategori ---
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // --- State: kata kunci search ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- State: kategori yang dipilih (null = semua) ---
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // --- State: hasil akhir setelah difilter search (otomatis reaktif) ---
    // Setiap kali _wisataList atau _searchQuery berubah, filteredList ikut update
    val filteredList: StateFlow<List<Wisata>> = combine(
        _wisataList,
        _searchQuery
    ) { list, query ->
        if (query.isEmpty()) {
            list
        } else {
            list.filter { wisata ->
                wisata.name.contains(query, ignoreCase = true) ||
                        wisata.location.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- State: loading & error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val wisataResponse = apiService.getWisata()
                if (wisataResponse.status) {
                    _wisataList.value = wisataResponse.data ?: emptyList()
                } else {
                    _errorMessage.value = wisataResponse.message
                }

                val categoryResponse = apiService.getCategories()
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

    // Dipanggil setiap user mengetik di search bar
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Dipanggil saat user memilih chip kategori
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
            // Reset search saat ganti kategori agar hasil tidak membingungkan
            _searchQuery.value = ""
            try {
                val response = apiService.getWisata()
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
            // Reset search saat ganti kategori agar hasil tidak membingungkan
            _searchQuery.value = ""
            try {
                val response = apiService.getWisataByCategory(categoryId)
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
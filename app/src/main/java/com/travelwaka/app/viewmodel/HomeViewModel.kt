package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.network.ApiClient
import com.travelwaka.app.network.model.Category
import com.travelwaka.app.network.model.Wisata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiService = ApiClient.apiService

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

    // Load wisata & kategori sekaligus saat pertama kali
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Jalankan keduanya, kategori tidak perlu tampilkan loading terpisah
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
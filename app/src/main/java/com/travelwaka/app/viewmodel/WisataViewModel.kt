package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.network.model.Category
import com.travelwaka.app.network.model.Wisata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.travelwaka.app.network.model.BookmarkItem
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WisataViewModel @Inject constructor(
    private val wisataRepository: WisataRepository
) : ViewModel() {

    // State wisata list
    private val _wisataList = MutableStateFlow<List<Wisata>>(emptyList())
    val wisataList: StateFlow<List<Wisata>> = _wisataList

    // State detail wisata
    private val _wisataDetail = MutableStateFlow<Wisata?>(null)
    val wisataDetail: StateFlow<Wisata?> = _wisataDetail

    // State categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // State loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _bookmarkMessage = MutableStateFlow<String?>(null)
    val bookmarkMessage: StateFlow<String?> = _bookmarkMessage.asStateFlow()
    // Ambil semua wisata
    fun getWisata() {
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

    // Ambil detail wisata
    fun getWisataDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _wisataDetail.value = null
            try {
                val response = wisataRepository.getWisataDetail(id)
                if (response.status) {
                    _wisataDetail.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat detail wisata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ambil wisata by kategori
    fun getWisataByCategory(categoryId: Int) {
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

    // Ambil semua kategori
    fun getCategories() {
        viewModelScope.launch {
            try {
                val response = wisataRepository.getCategories()
                if (response.status) {
                    _categories.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat kategori"
            }
        }
    }
}
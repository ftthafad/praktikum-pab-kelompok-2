package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.network.model.Review
import com.travelwaka.app.network.model.ReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val wisataRepository: WisataRepository
) : ViewModel() {

    // Daftar review publik untuk sebuah wisata
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Review milik user yang sedang login (null = belum pernah review)
    private val _myReview = MutableStateFlow<Review?>(null)
    val myReview: StateFlow<Review?> = _myReview.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Ambil semua review wisata tertentu (public)
    fun getReviews(wisataId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = wisataRepository.getReviews(wisataId)
                if (response.status) {
                    _reviews.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat ulasan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Cek apakah user sudah review wisata ini
    fun checkMyReview(wisataId: Int) {
        viewModelScope.launch {
            try {
                val response = wisataRepository.checkReview(wisataId)
                _myReview.value = response.data
            } catch (e: Exception) {
                _myReview.value = null
            }
        }
    }

    // Submit review (tambah atau update)
    fun submitReview(wisataId: Int, rating: Int, comment: String?, onSuccess: () -> Unit) {
        if (rating == 0) {
            _errorMessage.value = "Pilih rating terlebih dahulu"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            try {
                val response = wisataRepository.submitReview(
                    wisataId = wisataId,
                    request = ReviewRequest(rating = rating, comment = comment?.ifBlank { null })
                )
                if (response.status) {
                    _myReview.value = response.data
                    _successMessage.value = response.message
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengirim ulasan"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    // Hapus review
    fun deleteReview(wisataId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val response = wisataRepository.deleteReview(wisataId)
                if (response.status) {
                    _myReview.value = null
                    _successMessage.value = "Ulasan berhasil dihapus"
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus ulasan"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
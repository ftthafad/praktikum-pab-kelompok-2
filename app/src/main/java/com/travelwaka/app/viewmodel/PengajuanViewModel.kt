package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.network.model.Pengajuan
import com.travelwaka.app.network.model.PengajuanRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengajuanViewModel @Inject constructor(
    private val wisataRepository: WisataRepository
) : ViewModel() {

    // Status pengajuan user yang sedang login
    private val _pengajuanStatus = MutableStateFlow<Pengajuan?>(null)
    val pengajuanStatus: StateFlow<Pengajuan?> = _pengajuanStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Cek status pengajuan user
    fun getPengajuanStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = wisataRepository.getPengajuanStatus()
                _pengajuanStatus.value = response.data
            } catch (e: Exception) {
                _pengajuanStatus.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Kirim pengajuan jadi pengelola
    fun submitPengajuan(
        namaUsaha: String,
        deskripsi: String,
        alasan: String,
        onSuccess: () -> Unit
    ) {
        if (namaUsaha.isBlank()) {
            _errorMessage.value = "Nama usaha tidak boleh kosong"
            return
        }
        if (deskripsi.isBlank()) {
            _errorMessage.value = "Deskripsi tidak boleh kosong"
            return
        }
        if (alasan.isBlank()) {
            _errorMessage.value = "Alasan tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            try {
                val response = wisataRepository.submitPengajuan(
                    request = PengajuanRequest(
                        namaUsaha = namaUsaha,
                        deskripsi = deskripsi,
                        alasan = alasan
                    )
                )
                if (response.status) {
                    _pengajuanStatus.value = response.data
                    _successMessage.value = response.message
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengirim pengajuan"
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
package com.travelwaka.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.network.ApiClient
import com.travelwaka.app.network.model.Pengajuan
import com.travelwaka.app.network.model.RejectRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardApprovalViewModel : ViewModel() {

    private val apiService = ApiClient.apiService

    private val _pengajuanList = MutableStateFlow<List<Pengajuan>>(emptyList())
    val pengajuanList: StateFlow<List<Pengajuan>> = _pengajuanList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ID pengajuan yang sedang diproses (untuk disable tombol satu per satu)
    private val _processingId = MutableStateFlow<Int?>(null)
    val processingId: StateFlow<Int?> = _processingId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Ambil semua pengajuan pending
    fun getPengajuanList(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getSemuaPengajuan("Bearer $token")
                if (response.status) {
                    _pengajuanList.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data pengajuan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Approve pengajuan
    fun approvePengajuan(token: String, pengajuanId: Int) {
        viewModelScope.launch {
            _processingId.value = pengajuanId
            _errorMessage.value = null
            try {
                val response = apiService.approvePengajuan("Bearer $token", pengajuanId)
                if (response.status) {
                    // Hapus dari list setelah disetujui
                    _pengajuanList.value = _pengajuanList.value.filter { it.id != pengajuanId }
                    _successMessage.value = "Pengajuan berhasil disetujui"
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menyetujui pengajuan"
            } finally {
                _processingId.value = null
            }
        }
    }

    // Reject pengajuan dengan catatan
    fun rejectPengajuan(token: String, pengajuanId: Int, catatan: String) {
        if (catatan.isBlank()) {
            _errorMessage.value = "Catatan penolakan tidak boleh kosong"
            return
        }
        viewModelScope.launch {
            _processingId.value = pengajuanId
            _errorMessage.value = null
            try {
                val response = apiService.rejectPengajuan(
                    token = "Bearer $token",
                    id = pengajuanId,
                    request = RejectRequest(catatanAdmin = catatan)
                )
                if (response.status) {
                    // Hapus dari list setelah ditolak
                    _pengajuanList.value = _pengajuanList.value.filter { it.id != pengajuanId }
                    _successMessage.value = "Pengajuan berhasil ditolak"
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menolak pengajuan"
            } finally {
                _processingId.value = null
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
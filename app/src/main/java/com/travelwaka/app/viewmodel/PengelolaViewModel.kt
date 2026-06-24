package com.travelwaka.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.data.repository.WisataRepository
import com.travelwaka.app.data.repository.PengelolaRepository
import com.travelwaka.app.network.model.Category
import com.travelwaka.app.network.model.Wisata
import com.travelwaka.app.network.model.WisataRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PengelolaWisataViewModel @Inject constructor(
    private val wisataRepository: WisataRepository,
    private val pengelolaRepository: PengelolaRepository
) : ViewModel() {

    private val _wisataList = MutableStateFlow<List<Wisata>>(emptyList())
    val wisataList: StateFlow<List<Wisata>> = _wisataList.asStateFlow()

    // Wisata yang sedang diedit (null = mode tambah baru)
    private val _wisataDetail = MutableStateFlow<Wisata?>(null)
    val wisataDetail: StateFlow<Wisata?> = _wisataDetail.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Ambil semua wisata milik pengelola
    fun getWisataSaya() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = pengelolaRepository.getWisataSaya()
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

    // Set wisata yang sedang diedit (untuk FormWisataScreen)
    fun setWisataForEdit(wisata: Wisata?) {
        _wisataDetail.value = wisata
    }

    // Ambil detail wisata by ID langsung dari API/repository
    fun loadWisataForEdit(wisataId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = wisataRepository.getWisataDetail(wisataId)
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

    // Tambah wisata baru
    fun tambahWisata(
        name: String,
        description: String,
        location: String,
        latitude: Double?,
        longitude: Double?,
        price: String,
        openingHours: String,
        categoryId: Int,
        onSuccess: (Int) -> Unit  // callback bawa wisataId baru untuk upload foto
    ) {
        if (!validateForm(name, description, location, price, openingHours, categoryId)) return

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                val response = pengelolaRepository.tambahWisata(
                    request = WisataRequest(
                        name = name,
                        description = description,
                        location = location,
                        latitude = latitude,
                        longitude = longitude,
                        price = price,
                        openingHours = openingHours,
                        categoryId = categoryId
                    )
                )
                if (response.status) {
                    _successMessage.value = "Wisata berhasil ditambahkan"
                    response.data?.let { onSuccess(it.id) }
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambah wisata"
            } finally {
                _isSaving.value = false
            }
        }
    }

    // Update wisata
    fun updateWisata(
        wisataId: Int,
        name: String,
        description: String,
        location: String,
        latitude: Double?,
        longitude: Double?,
        price: String,
        openingHours: String,
        categoryId: Int,
        onSuccess: () -> Unit
    ) {
        if (!validateForm(name, description, location, price, openingHours, categoryId)) return

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                val response = pengelolaRepository.updateWisata(
                    id = wisataId,
                    request = WisataRequest(
                        name = name,
                        description = description,
                        location = location,
                        latitude = latitude,
                        longitude = longitude,
                        price = price,
                        openingHours = openingHours,
                        categoryId = categoryId
                    )
                )
                if (response.status) {
                    _successMessage.value = "Wisata berhasil diupdate"
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengupdate wisata"
            } finally {
                _isSaving.value = false
            }
        }
    }

    // Hapus wisata
    fun deleteWisata(wisataId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = pengelolaRepository.deleteWisata(wisataId)
                if (response.status) {
                    _wisataList.value = _wisataList.value.filter { it.id != wisataId }
                    _successMessage.value = "Wisata berhasil dihapus"
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus wisata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Upload foto wisata
    fun uploadFoto(context: Context, wisataId: Int, uri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isUploading.value = true
            _errorMessage.value = null
            try {
                val bytes = com.travelwaka.app.utils.ImageCompressor.compressImage(context, uri)
                    ?: run {
                        val inputStream = context.contentResolver.openInputStream(uri)
                            ?: throw Exception("Tidak bisa membaca file")
                        val readBytes = inputStream.readBytes()
                        inputStream.close()
                        readBytes
                    }

                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("photo", "photo.jpg", requestBody)

                val response = pengelolaRepository.uploadFotoWisata(
                    wisataId = wisataId,
                    photo = part
                )
                if (response.status) {
                    _successMessage.value = "Foto berhasil diupload"
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                if (com.travelwaka.app.BuildConfig.DEBUG) {
                    android.util.Log.e("PengelolaVM", "Error uploading photo", e)
                }
                _errorMessage.value = "Gagal mengupload foto: ${e.localizedMessage ?: e.message}"
            } finally {
                _isUploading.value = false
            }
        }
    }

    // Hapus foto
    fun deleteFoto(wisataId: Int, photoId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = pengelolaRepository.deleteFotoWisata(wisataId, photoId)
                if (response.status) {
                    _successMessage.value = "Foto berhasil dihapus"
                    onSuccess()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus foto"
            }
        }
    }

    private fun validateForm(
        name: String,
        description: String,
        location: String,
        price: String,
        openingHours: String,
        categoryId: Int
    ): Boolean {
        return when {
            name.isBlank() -> { _errorMessage.value = "Nama wisata tidak boleh kosong"; false }
            description.isBlank() -> { _errorMessage.value = "Deskripsi tidak boleh kosong"; false }
            location.isBlank() -> { _errorMessage.value = "Lokasi tidak boleh kosong"; false }
            price.isBlank() -> { _errorMessage.value = "Harga tiket tidak boleh kosong"; false }
            openingHours.isBlank() -> { _errorMessage.value = "Jam operasional tidak boleh kosong"; false }
            categoryId == 0 -> { _errorMessage.value = "Pilih kategori wisata"; false }
            else -> true
        }
    }

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

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}

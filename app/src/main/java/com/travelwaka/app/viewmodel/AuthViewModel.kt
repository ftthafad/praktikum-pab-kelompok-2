package com.travelwaka.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.LoginRequest
import com.travelwaka.app.network.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {
    private val tokenDataStore = TokenDataStore.getInstance(context)

    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State untuk error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // State untuk sukses
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // Data user
    val userName = tokenDataStore.userName
    val userEmail = tokenDataStore.userEmail
    val userRole = tokenDataStore.userRole

    // Login
    fun login(email: String, password: String) {
        // Validasi lokal dulu sebelum hit API
        if (email.isBlank()) {
            _errorMessage.value = "Email tidak boleh kosong"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Format email tidak valid"
            return
        }
        if (password.isBlank()) {
            _errorMessage.value = "Password tidak boleh kosong"
            return
        }
        if (password.length < 8) {
            _errorMessage.value = "Email atau password tidak valid"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.status) {
                    response.data?.let { data ->
                        tokenDataStore.saveAuth(
                            token = data.token,
                            role = data.user.role,
                            name = data.user.name,
                            email = data.user.email
                        )
                    }
                    _isSuccess.value = true
                } else {
                    // Pakai pesan umum, abaikan response.message dari server
                    _errorMessage.value = "Email atau password tidak valid"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Register
    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        if (name.isBlank()) {
            _errorMessage.value = "Nama tidak boleh kosong"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Format email tidak valid"
            return
        }
        if (password.length < 8) {
            _errorMessage.value = "Password harus minimal 8 karakter"
            return
        }
        if (password != passwordConfirmation) {
            _errorMessage.value = "Konfirmasi password tidak cocok"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.register(
                    RegisterRequest(name, email, password, passwordConfirmation)
                )
                if (response.status) {
                    response.data?.let { data ->
                        tokenDataStore.saveAuth(
                            token = data.token,
                            role = data.user.role,
                            name = data.user.name,
                            email = data.user.email
                        )
                    }
                    _isSuccess.value = true
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Logout
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    apiService.logout("Bearer $token")
                }
            } catch (e: Exception) {
                // ignore error logout dari server
            } finally {
                tokenDataStore.clearAuth()
                _isSuccess.value = false
                _isLoading.value = false
                onSuccess()
            }
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}
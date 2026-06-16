package com.travelwaka.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.data.repository.AuthRepository
import com.travelwaka.app.network.model.LoginRequest
import com.travelwaka.app.network.model.RegisterRequest
import com.travelwaka.app.network.model.GoogleLoginRequest
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
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
    private val tokenDataStore: TokenDataStore,
    private val authRepository: AuthRepository
) : ViewModel() {

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
    val token = tokenDataStore.token
    val userName = tokenDataStore.userName
    val userEmail = tokenDataStore.userEmail
    val userRole = tokenDataStore.userRole

    private val _currentUser = MutableStateFlow<com.travelwaka.app.network.model.User?>(null)
    val currentUser: StateFlow<com.travelwaka.app.network.model.User?> = _currentUser

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val tokenVal = tokenDataStore.token.first()
                if (!tokenVal.isNullOrEmpty()) {
                    val response = authRepository.me()
                    if (response.status) {
                        _currentUser.value = response.data
                        // Auto-sync user details if changed in DB
                        response.data?.let { user ->
                            tokenDataStore.saveAuth(
                                token = tokenVal,
                                role = user.role,
                                name = user.name,
                                email = user.email
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

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
                val response = authRepository.login(LoginRequest(email, password))
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
                val response = authRepository.register(
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

    // Google Sign-In
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = authRepository.loginWithGoogle(GoogleLoginRequest(idToken))
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
                _errorMessage.value = "Gagal masuk dengan Google"
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
                // Hapus state kredensial Google Sign-In
                val credentialManager = CredentialManager.create(context)
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    // abaikan jika gagal
                }

                val token = tokenDataStore.token.first()
                if (!token.isNullOrEmpty()) {
                    authRepository.logout()
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
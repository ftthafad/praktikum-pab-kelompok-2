package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): AuthResponse = apiService.register(request)
    override suspend fun login(request: LoginRequest): AuthResponse = apiService.login(request)
    override suspend fun loginWithGoogle(request: GoogleLoginRequest): AuthResponse = apiService.loginWithGoogle(request)
    override suspend fun logout(): AuthResponse = apiService.logout()
    override suspend fun me(): MeResponse = apiService.me()
}

package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.*

interface AuthRepository {
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun loginWithGoogle(request: GoogleLoginRequest): AuthResponse
    suspend fun logout(): AuthResponse
    suspend fun me(): MeResponse
}

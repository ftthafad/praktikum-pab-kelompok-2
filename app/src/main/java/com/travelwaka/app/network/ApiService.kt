package com.travelwaka.app.network

import com.travelwaka.app.network.model.*
import okhttp3.MultipartBody
import retrofit2.http.*
interface ApiService {

    // ✅ Auth
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): AuthResponse

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") token: String
    ): MeResponse

    // ✅ Wisata
    @GET("wisata")
    suspend fun getWisata(): WisataListResponse

    @GET("wisata/{id}")
    suspend fun getWisataDetail(
        @Path("id") id: Int
    ): WisataDetailResponse

    @GET("wisata/category/{categoryId}")
    suspend fun getWisataByCategory(
        @Path("categoryId") categoryId: Int
    ): WisataListResponse

    // ✅ Categories
    @GET("categories")
    suspend fun getCategories(): CategoryListResponse

    // Ambil semua bookmark user
    @GET("bookmarks")
    suspend fun getBookmarks(
        @Header("Authorization") token: String
    ): BookmarkListResponse

    // Toggle bookmark (add/remove)
    @POST("bookmarks/{wisataId}")
    suspend fun toggleBookmark(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int
    ): BookmarkStatusResponse

    // Cek status bookmark wisata tertentu
    @GET("bookmarks/{wisataId}")
    suspend fun checkBookmark(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int
    ): BookmarkStatusResponse

    // ── Review ────────────────────────────────────────────────────────────────

    @GET("wisata/{wisataId}/reviews")
    suspend fun getReviews(@Path("wisataId") wisataId: Int): ReviewListResponse

    @GET("wisata/{wisataId}/reviews/check")
    suspend fun checkReview(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int
    ): ReviewCheckResponse

    @POST("wisata/{wisataId}/reviews")
    suspend fun submitReview(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int,
        @Body request: ReviewRequest
    ): ReviewResponse

    @DELETE("wisata/{wisataId}/reviews")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int
    ): SimpleResponse

    // ── Pengajuan Pengelola ───────────────────────────────────────────────────

    @POST("pengajuan")
    suspend fun submitPengajuan(
        @Header("Authorization") token: String,
        @Body request: PengajuanRequest
    ): PengajuanResponse

    @GET("pengajuan/status")
    suspend fun getPengajuanStatus(
        @Header("Authorization") token: String
    ): PengajuanResponse

    // ── Super Admin — Approval ────────────────────────────────────────────────

    @GET("admin/pengajuan")
    suspend fun getSemuaPengajuan(
        @Header("Authorization") token: String
    ): PengajuanListResponse

    @POST("admin/pengajuan/{id}/approve")
    suspend fun approvePengajuan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): SimpleResponse

    @POST("admin/pengajuan/{id}/reject")
    suspend fun rejectPengajuan(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: RejectRequest
    ): SimpleResponse

    // ── Pengelola — CRUD Wisata ───────────────────────────────────────────────

    @GET("pengelola/wisata")
    suspend fun getWisataSaya(
        @Header("Authorization") token: String
    ): WisataSayaListResponse

    @POST("pengelola/wisata")
    suspend fun tambahWisata(
        @Header("Authorization") token: String,
        @Body request: WisataRequest
    ): WisataSayaResponse

    @PUT("pengelola/wisata/{id}")
    suspend fun updateWisata(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: WisataRequest
    ): WisataSayaResponse

    @DELETE("pengelola/wisata/{id}")
    suspend fun deleteWisata(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): SimpleResponse

    @Multipart
    @POST("pengelola/wisata/{wisataId}/photos")
    suspend fun uploadFotoWisata(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int,
        @Part photo: MultipartBody.Part
    ): PhotoUploadResponse

    @DELETE("pengelola/wisata/{wisataId}/photos/{photoId}")
    suspend fun deleteFotoWisata(
        @Header("Authorization") token: String,
        @Path("wisataId") wisataId: Int,
        @Path("photoId") photoId: Int
    ): SimpleResponse
}
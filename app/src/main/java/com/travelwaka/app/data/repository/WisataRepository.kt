package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.*
import okhttp3.MultipartBody

interface WisataRepository {
    suspend fun getWisata(): WisataListResponse
    suspend fun getWisataDetail(id: Int): WisataDetailResponse
    suspend fun getWisataByCategory(categoryId: Int): WisataListResponse
    suspend fun getCategories(): CategoryListResponse
    suspend fun getBookmarks(): BookmarkListResponse
    suspend fun toggleBookmark(wisataId: Int): BookmarkStatusResponse
    suspend fun checkBookmark(wisataId: Int): BookmarkStatusResponse
    suspend fun getReviews(wisataId: Int): ReviewListResponse
    suspend fun checkReview(wisataId: Int): ReviewCheckResponse
    suspend fun submitReview(wisataId: Int, request: ReviewRequest): ReviewResponse
    suspend fun deleteReview(wisataId: Int): SimpleResponse
    suspend fun submitPengajuan(request: PengajuanRequest): PengajuanResponse
    suspend fun getPengajuanStatus(): PengajuanResponse
    suspend fun getSemuaPengajuan(): PengajuanListResponse
    suspend fun approvePengajuan(id: Int): SimpleResponse
    suspend fun rejectPengajuan(id: Int, request: RejectRequest): SimpleResponse
    suspend fun getWisataSaya(): WisataSayaListResponse
    suspend fun tambahWisata(request: WisataRequest): WisataSayaResponse
    suspend fun updateWisata(id: Int, request: WisataRequest): WisataSayaResponse
    suspend fun deleteWisata(id: Int): SimpleResponse
    suspend fun uploadFotoWisata(wisataId: Int, photo: MultipartBody.Part): PhotoUploadResponse
    suspend fun deleteFotoWisata(wisataId: Int, photoId: Int): SimpleResponse
}

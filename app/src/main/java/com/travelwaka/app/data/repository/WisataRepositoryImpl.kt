package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.*
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WisataRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WisataRepository {
    override suspend fun getWisata(): WisataListResponse = apiService.getWisata()
    override suspend fun getWisataDetail(id: Int): WisataDetailResponse = apiService.getWisataDetail(id)
    override suspend fun getWisataByCategory(categoryId: Int): WisataListResponse = apiService.getWisataByCategory(categoryId)
    override suspend fun getCategories(): CategoryListResponse = apiService.getCategories()
    override suspend fun getBookmarks(): BookmarkListResponse = apiService.getBookmarks()
    override suspend fun toggleBookmark(wisataId: Int): BookmarkStatusResponse = apiService.toggleBookmark(wisataId)
    override suspend fun checkBookmark(wisataId: Int): BookmarkStatusResponse = apiService.checkBookmark(wisataId)
    override suspend fun getReviews(wisataId: Int): ReviewListResponse = apiService.getReviews(wisataId)
    override suspend fun checkReview(wisataId: Int): ReviewCheckResponse = apiService.checkReview(wisataId)
    override suspend fun submitReview(wisataId: Int, request: ReviewRequest): ReviewResponse = apiService.submitReview(wisataId, request)
    override suspend fun deleteReview(wisataId: Int): SimpleResponse = apiService.deleteReview(wisataId)
    override suspend fun submitPengajuan(request: PengajuanRequest): PengajuanResponse = apiService.submitPengajuan(request)
    override suspend fun getPengajuanStatus(): PengajuanResponse = apiService.getPengajuanStatus()
    override suspend fun getSemuaPengajuan(): PengajuanListResponse = apiService.getSemuaPengajuan()
    override suspend fun approvePengajuan(id: Int): SimpleResponse = apiService.approvePengajuan(id)
    override suspend fun rejectPengajuan(id: Int, request: RejectRequest): SimpleResponse = apiService.rejectPengajuan(id, request)
    override suspend fun getWisataSaya(): WisataSayaListResponse = apiService.getWisataSaya()
    override suspend fun tambahWisata(request: WisataRequest): WisataSayaResponse = apiService.tambahWisata(request)
    override suspend fun updateWisata(id: Int, request: WisataRequest): WisataSayaResponse = apiService.updateWisata(id, request)
    override suspend fun deleteWisata(id: Int): SimpleResponse = apiService.deleteWisata(id)
    override suspend fun uploadFotoWisata(wisataId: Int, photo: MultipartBody.Part): PhotoUploadResponse = apiService.uploadFotoWisata(wisataId, photo)
    override suspend fun deleteFotoWisata(wisataId: Int, photoId: Int): SimpleResponse = apiService.deleteFotoWisata(wisataId, photoId)
}

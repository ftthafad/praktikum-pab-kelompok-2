package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.PhotoUploadResponse
import com.travelwaka.app.network.model.SimpleResponse
import com.travelwaka.app.network.model.WisataSayaListResponse
import com.travelwaka.app.network.model.WisataSayaResponse
import com.travelwaka.app.network.model.WisataRequest
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PengelolaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PengelolaRepository {
    override suspend fun getWisataSaya(): WisataSayaListResponse = apiService.getWisataSaya()
    override suspend fun tambahWisata(request: WisataRequest): WisataSayaResponse = apiService.tambahWisata(request)
    override suspend fun updateWisata(id: Int, request: WisataRequest): WisataSayaResponse = apiService.updateWisata(id, request)
    override suspend fun deleteWisata(id: Int): SimpleResponse = apiService.deleteWisata(id)
    override suspend fun uploadFotoWisata(wisataId: Int, photo: MultipartBody.Part): PhotoUploadResponse = apiService.uploadFotoWisata(wisataId, photo)
    override suspend fun deleteFotoWisata(wisataId: Int, photoId: Int): SimpleResponse = apiService.deleteFotoWisata(wisataId, photoId)
}

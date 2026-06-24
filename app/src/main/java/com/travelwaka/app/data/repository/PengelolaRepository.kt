package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.PhotoUploadResponse
import com.travelwaka.app.network.model.SimpleResponse
import com.travelwaka.app.network.model.WisataSayaListResponse
import com.travelwaka.app.network.model.WisataSayaResponse
import com.travelwaka.app.network.model.WisataRequest
import okhttp3.MultipartBody

interface PengelolaRepository {
    suspend fun getWisataSaya(): WisataSayaListResponse
    suspend fun tambahWisata(request: WisataRequest): WisataSayaResponse
    suspend fun updateWisata(id: Int, request: WisataRequest): WisataSayaResponse
    suspend fun deleteWisata(id: Int): SimpleResponse
    suspend fun uploadFotoWisata(wisataId: Int, photo: MultipartBody.Part): PhotoUploadResponse
    suspend fun deleteFotoWisata(wisataId: Int, photoId: Int): SimpleResponse
}

package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.PengajuanListResponse
import com.travelwaka.app.network.model.PengajuanRequest
import com.travelwaka.app.network.model.PengajuanResponse
import com.travelwaka.app.network.model.RejectRequest
import com.travelwaka.app.network.model.SimpleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PengajuanRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PengajuanRepository {
    override suspend fun submitPengajuan(request: PengajuanRequest): PengajuanResponse = apiService.submitPengajuan(request)
    override suspend fun getPengajuanStatus(): PengajuanResponse = apiService.getPengajuanStatus()
    override suspend fun getSemuaPengajuan(): PengajuanListResponse = apiService.getSemuaPengajuan()
    override suspend fun approvePengajuan(id: Int): SimpleResponse = apiService.approvePengajuan(id)
    override suspend fun rejectPengajuan(id: Int, request: RejectRequest): SimpleResponse = apiService.rejectPengajuan(id, request)
}

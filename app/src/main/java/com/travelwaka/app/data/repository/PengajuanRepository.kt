package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.PengajuanListResponse
import com.travelwaka.app.network.model.PengajuanRequest
import com.travelwaka.app.network.model.PengajuanResponse
import com.travelwaka.app.network.model.RejectRequest
import com.travelwaka.app.network.model.SimpleResponse

interface PengajuanRepository {
    suspend fun submitPengajuan(request: PengajuanRequest): PengajuanResponse
    suspend fun getPengajuanStatus(): PengajuanResponse
    suspend fun getSemuaPengajuan(): PengajuanListResponse
    suspend fun approvePengajuan(id: Int): SimpleResponse
    suspend fun rejectPengajuan(id: Int, request: RejectRequest): SimpleResponse
}

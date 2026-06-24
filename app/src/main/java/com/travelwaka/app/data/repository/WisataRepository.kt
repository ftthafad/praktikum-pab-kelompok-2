package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.CategoryListResponse
import com.travelwaka.app.network.model.WisataDetailResponse
import com.travelwaka.app.network.model.WisataListResponse

interface WisataRepository {
    suspend fun getWisata(): WisataListResponse
    suspend fun getWisataDetail(id: Int): WisataDetailResponse
    suspend fun getWisataByCategory(categoryId: Int): WisataListResponse
    suspend fun getCategories(): CategoryListResponse
}

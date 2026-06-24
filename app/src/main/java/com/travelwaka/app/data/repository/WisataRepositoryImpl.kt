package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.CategoryListResponse
import com.travelwaka.app.network.model.WisataDetailResponse
import com.travelwaka.app.network.model.WisataListResponse
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
}

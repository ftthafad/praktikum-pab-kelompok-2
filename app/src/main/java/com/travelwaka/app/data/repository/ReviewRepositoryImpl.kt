package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.ReviewCheckResponse
import com.travelwaka.app.network.model.ReviewListResponse
import com.travelwaka.app.network.model.ReviewRequest
import com.travelwaka.app.network.model.ReviewResponse
import com.travelwaka.app.network.model.SimpleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ReviewRepository {
    override suspend fun getReviews(wisataId: Int): ReviewListResponse = apiService.getReviews(wisataId)
    override suspend fun checkReview(wisataId: Int): ReviewCheckResponse = apiService.checkReview(wisataId)
    override suspend fun submitReview(wisataId: Int, request: ReviewRequest): ReviewResponse = apiService.submitReview(wisataId, request)
    override suspend fun deleteReview(wisataId: Int): SimpleResponse = apiService.deleteReview(wisataId)
}

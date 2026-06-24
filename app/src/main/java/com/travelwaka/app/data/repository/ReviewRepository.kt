package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.ReviewCheckResponse
import com.travelwaka.app.network.model.ReviewListResponse
import com.travelwaka.app.network.model.ReviewRequest
import com.travelwaka.app.network.model.ReviewResponse
import com.travelwaka.app.network.model.SimpleResponse

interface ReviewRepository {
    suspend fun getReviews(wisataId: Int): ReviewListResponse
    suspend fun checkReview(wisataId: Int): ReviewCheckResponse
    suspend fun submitReview(wisataId: Int, request: ReviewRequest): ReviewResponse
    suspend fun deleteReview(wisataId: Int): SimpleResponse
}

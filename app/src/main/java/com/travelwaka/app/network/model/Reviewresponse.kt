package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class Review(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("wisata_id") val wisataId: Int,
    val rating: Int,
    val comment: String?,
    val user: ReviewUser?,
    @SerializedName("created_at") val createdAt: String?
)

data class ReviewUser(
    val id: Int,
    val name: String
)

data class ReviewListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Review>?
)

data class ReviewResponse(
    val status: Boolean,
    val message: String,
    val data: Review?
)

data class ReviewCheckResponse(
    val status: Boolean,
    val message: String,
    val data: Review?  // null = belum review
)

data class ReviewRequest(
    val rating: Int,
    val comment: String?
)
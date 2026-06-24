package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class Wisata(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("category_id") val categoryId: Int,
    val name: String,
    val description: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val price: String,
    @SerializedName("opening_hours") val openingHours: String?,
    val rating: Float,
    @SerializedName("review_count") val reviewCount: Int,
    val category: Category?,
    val photos: List<Photo>?,
    @SerializedName("cover_photo") val coverPhoto: Photo?,
    @SerializedName("created_at") val createdAt: String?
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String?
)

data class Photo(
    val id: Int,
    @SerializedName("wisata_id") val wisataId: Int,
    @SerializedName("photo_url") val photoUrl: String,
    @SerializedName("is_cover") val isCover: Boolean
)
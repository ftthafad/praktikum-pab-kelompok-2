package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class WisataSayaListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Wisata>?
)

data class WisataSayaResponse(
    val status: Boolean,
    val message: String,
    val data: Wisata?
)

data class WisataRequest(
    val name: String,
    val description: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val price: String,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("category_id") val categoryId: Int
)

data class PhotoUploadResponse(
    val status: Boolean,
    val message: String,
    val data: Photo?
)

data class SimpleResponse(
    val status: Boolean,
    val message: String
)
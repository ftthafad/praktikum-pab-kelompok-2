package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("bookmarks_count") val bookmarksCount: Int? = 0,
    @SerializedName("reviews_count") val reviewsCount: Int? = 0,
    @SerializedName("wisata_count") val wisataCount: Int? = 0
)
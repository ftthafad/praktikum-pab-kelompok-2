package com.travelwaka.app.network.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar: String?,
    val created_at: String?,
    val bookmarks_count: Int? = 0,
    val reviews_count: Int? = 0,
    val wisata_count: Int? = 0
)
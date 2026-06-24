package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class Pengajuan(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nama_usaha") val namaUsaha: String,
    val deskripsi: String,
    val alasan: String,
    val status: String, // "pending" | "approved" | "rejected"
    @SerializedName("catatan_admin") val catatanAdmin: String?,
    val user: PengajuanUser?,
    @SerializedName("created_at") val createdAt: String?
)

data class PengajuanUser(
    val id: Int,
    val name: String,
    val email: String
)

data class PengajuanResponse(
    val status: Boolean,
    val message: String,
    val data: Pengajuan?
)

data class PengajuanListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Pengajuan>?
)

data class PengajuanRequest(
    @SerializedName("nama_usaha") val namaUsaha: String,
    val deskripsi: String,
    val alasan: String
)

data class RejectRequest(
    @SerializedName("catatan_admin") val catatanAdmin: String
)
package com.travelwaka.app.network.model

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    @SerializedName("idToken")
    val idToken: String,
    
    @SerializedName("id_token")
    val idTokenSnake: String = idToken
)

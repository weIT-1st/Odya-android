package com.weit.data.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KakaoAccessToken(
    @field:Json(name = "accessToken") val token: String,
)

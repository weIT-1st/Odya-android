package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmToken(
    @field:Json(name = "fcmToken") val fcmToken: String,
)

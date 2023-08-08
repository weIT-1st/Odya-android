package com.weit.data.model.favoritePlace

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoritePlaceRegistration(
    @field:Json(name = "placeId") val placeId: String,
)

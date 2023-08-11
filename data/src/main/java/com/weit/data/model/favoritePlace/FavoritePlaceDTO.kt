package com.weit.data.model.favoritePlace

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoritePlaceDTO(
    @field:Json(name = "id") val favoritePlaceId: Long,
    @field:Json(name = "placeId") val placeId: String,
    @field:Json(name = "userId") val userId: Long,
)

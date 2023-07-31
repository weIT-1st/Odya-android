package com.weit.data.model.favoritePlace

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoritePlaceListDTO(
    @field:Json(name = "content") val reviews: List<FavoritePlaceDTO>,
    @field:Json(name = "hasNext") val hasNext: Boolean,
)

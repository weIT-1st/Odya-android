package com.weit.data.model.image

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserImageResponseDTO(
    @field:Json(name = "imageId") val imageId: Long,
    @field:Json(name = "imageUrl") val imageUrl: String,
    @field:Json(name = "placeId") val placeId: Long,
    @field:Json(name = "isLifeShot") val isLifeShot: Boolean,
    @field:Json(name = "placeName") val placeName: String,
    @field:Json(name = "journalId") val journalId: Long,
    @field:Json(name = "communityId") val communityId: Long,
)

package com.weit.data.model.image

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoordinateUserImageResponseDTO(
    @field:Json(name = "imageId") val imageId: Long,
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "imageUrl") val imageUrl: String,
    @field:Json(name = "placeId") val placeId: Long,
    @field:Json(name = "latitude") val latitude: Double,
    @field:Json(name = "longitude") val longitude: Double,
    @field:Json(name = "imageUserType") val imageUserType: String,
    @field:Json(name = "journalId") val journalId: Long,
    @field:Json(name = "communityId") val communityId: Long,
)

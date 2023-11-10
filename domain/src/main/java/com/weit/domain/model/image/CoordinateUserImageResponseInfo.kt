package com.weit.domain.model.image

data class CoordinateUserImageResponseInfo(
    val imageId: Long,
    val userId: Long,
    val imageUrl: String,
    val placeId: Long,
    val latitude: Double,
    val longitude: Double,
    val imageUserType: String,
    val journalId: Long,
    val communityId: Long,
)

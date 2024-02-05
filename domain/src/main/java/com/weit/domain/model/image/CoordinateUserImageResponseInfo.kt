package com.weit.domain.model.image

import com.weit.domain.model.user.ImageUserType

data class CoordinateUserImageResponseInfo(
    val imageId: Long,
    val userId: Long,
    val imageUrl: String,
    val placeId: String,
    val latitude: Double,
    val longitude: Double,
    val imageUserType: ImageUserType,
    val journalId: Long,
    val communityId: Long,
)

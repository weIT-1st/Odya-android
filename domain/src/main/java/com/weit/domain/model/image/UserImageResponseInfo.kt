package com.weit.domain.model.image

data class UserImageResponseInfo(
    val imageId: Long,
    val imageUrl: String,
    val placeId: String?,
    val isLifeShot: Boolean,
    val placeName: String?,
    val journalId: Long?,
    val communityId: Long?,
)

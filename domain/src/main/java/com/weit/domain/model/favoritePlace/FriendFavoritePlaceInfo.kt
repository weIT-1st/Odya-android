package com.weit.domain.model.favoritePlace

data class FriendFavoritePlaceInfo(
    val userId: Long,
    val size: Int? = 4,
    val sortType: String? = "LATEST",
    val lastFavoritePlaceId: Long? = null,
)

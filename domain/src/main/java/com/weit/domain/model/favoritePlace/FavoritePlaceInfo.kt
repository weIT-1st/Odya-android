package com.weit.domain.model.favoritePlace

data class FavoritePlaceInfo(
    val size: Int? = 10,
    val sortType: String? = "LATEST",
    val lastFavoritePlaceId: Long? = null,
)

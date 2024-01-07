package com.weit.domain.model.favoritePlace

data class FavoritePlaceDetail(
    val favoritePlaceId: Long,
    val placeId: String,
    val userId: Long,
    val isFavoritePlace: Boolean
)

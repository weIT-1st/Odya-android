package com.weit.presentation.ui.profile.otherprofile.favoriteplace

data class OtherFavoritePlaceEntity(
    val favoritePlaceId: Long,
    val placeId: String?,
    val placeName: String?,
    val placeAddress: String?,
    val isFavoritePlace: Boolean
)

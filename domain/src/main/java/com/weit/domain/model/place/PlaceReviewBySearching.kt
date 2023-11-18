package com.weit.domain.model.place

data class PlaceReviewBySearching(
    val hasNext: Boolean,
    val averageRating: Float,
    val content: List<PlaceReviewContent>,
)

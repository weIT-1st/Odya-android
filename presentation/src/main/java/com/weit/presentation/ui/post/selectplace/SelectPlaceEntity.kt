package com.weit.presentation.ui.post.selectplace

import com.weit.domain.model.place.PlacePrediction

data class SelectPlaceEntity(
    val place: PlacePrediction,
    val isSelected: Boolean,
)

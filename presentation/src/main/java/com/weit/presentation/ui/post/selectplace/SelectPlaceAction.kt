package com.weit.presentation.ui.post.selectplace

import com.weit.domain.model.place.PlacePrediction

sealed class SelectPlaceAction {
    data class OnClickPlace(
        val placePrediction: PlacePrediction,
    ) : SelectPlaceAction()
}

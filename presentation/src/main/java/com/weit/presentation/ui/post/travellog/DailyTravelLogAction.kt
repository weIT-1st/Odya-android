package com.weit.presentation.ui.post.travellog

import com.weit.domain.model.place.PlacePrediction

sealed class DailyTravelLogAction {
    data class OnSelectPictureClick(
        val position: Int,
    ) : DailyTravelLogAction()

    data class OnDeletePicture(
        val position: Int,
        val imageIndex: Int,
    ) : DailyTravelLogAction()

    data class OnDeleteDailyTravelLog(
        val position: Int,
    ) : DailyTravelLogAction()

    data class OnSelectPlace(
        val currentPlace: PlacePrediction?,
        val images: List<String>,
    ) : DailyTravelLogAction()
}

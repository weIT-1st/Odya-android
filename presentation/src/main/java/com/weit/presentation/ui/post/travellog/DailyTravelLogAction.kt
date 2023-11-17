package com.weit.presentation.ui.post.travellog

sealed class DailyTravelLogAction {
    data class OnSelectPicture(
        val position: Int,
    ) : DailyTravelLogAction()

    data class OnPickDate(
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
        val position: Int,
        val images: List<String>,
    ) : DailyTravelLogAction()
}

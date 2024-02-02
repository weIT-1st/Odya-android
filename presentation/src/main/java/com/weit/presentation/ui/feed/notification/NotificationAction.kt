package com.weit.presentation.ui.feed.notification


sealed class NotificationAction {
    data class OnClickFeed(
        val feedId: Long,
        val userName: String,
    ) : NotificationAction()

    data class OnClickTravelJournal(
        val journalId: Long,
    ) : NotificationAction()

    data class OnClickOtherProfile(
        val userName: String,
    ) : NotificationAction()
}

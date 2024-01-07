package com.weit.presentation.ui.feed.post.traveljournal

import com.weit.domain.model.place.PlacePrediction

sealed class FeedTravelJournalAction {
    data class OnClickPublicJournal(
        val journal: FeedTravelJournalEntity,
    ) : FeedTravelJournalAction()

    data class OnClickPrivateJournal(
        val journal: FeedTravelJournalEntity,
    ) : FeedTravelJournalAction()
}

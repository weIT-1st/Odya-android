package com.weit.presentation.ui.feed.post.traveljournal

import com.weit.domain.model.journal.TravelJournalListInfo

data class FeedTravelJournalEntity(
    val travelJournal: TravelJournalListInfo,
    val isSelected: Boolean,
)

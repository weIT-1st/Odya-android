package com.weit.presentation.model.feed

import android.os.Parcelable
import com.weit.domain.model.journal.TravelCompanionSimpleResponsesInfo
import com.weit.domain.model.journal.TravelJournalWriterInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectTravelJournalDTO(
    val travelJournalId: Long,
    val travelJournalTitle: String,
) : Parcelable

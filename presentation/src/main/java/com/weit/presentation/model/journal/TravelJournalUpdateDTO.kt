package com.weit.presentation.model.journal

import android.os.Parcelable
import com.weit.domain.model.journal.TravelJournalContentsInfo
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TravelJournalUpdateDTO (
    val travelJournalId: Long,
    val title: String?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val visibility: String?,
) : Parcelable

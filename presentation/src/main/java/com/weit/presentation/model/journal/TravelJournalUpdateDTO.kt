package com.weit.presentation.model.journal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TravelJournalUpdateDTO (
    val title: String?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val visibility: String?,
//    val travelJournalCompanions: List<TravelJournalCompanionsInfo>,
) : Parcelable

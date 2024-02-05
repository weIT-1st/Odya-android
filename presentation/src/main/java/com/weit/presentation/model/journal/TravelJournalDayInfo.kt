package com.weit.presentation.model.journal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TravelJournalDayInfo(
    private val travelJournalContentId: Long,
    private val day: Int
) : Parcelable

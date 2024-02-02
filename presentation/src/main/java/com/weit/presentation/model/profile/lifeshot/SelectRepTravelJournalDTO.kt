package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectRepTravelJournalDTO(
    val repJournalId: Long,
    val journalId: Long,
) : Parcelable

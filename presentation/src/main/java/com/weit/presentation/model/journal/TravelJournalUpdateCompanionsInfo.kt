package com.weit.presentation.model.journal

import android.os.Parcelable
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TravelJournalUpdateCompanionsInfo (
    val userId: Long,
    val nickname: String,
    val profileUrl: String,
    val isRegistered: Boolean,
    val isFollowing: Boolean
) : Parcelable

package com.weit.domain.model.journal

import com.weit.domain.model.user.UserProfile

data class TravelJournalWriterInfo(
    val userId: Long,
    val nickname: String,
    val profile: UserProfile,
)

package com.weit.domain.model.journal

data class TravelJournalCompanionsInfo (
    val userId: Long,
    val nickname: String,
    val profileUrl: String,
    val isRegistered: Boolean,
    val isFollowing: Boolean
)

package com.weit.domain.model.user

data class UserContent (
    val userId: Long,
    val nickname: String,
    val profile: UserProfile
)

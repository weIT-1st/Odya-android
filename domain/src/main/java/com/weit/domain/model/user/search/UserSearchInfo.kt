package com.weit.domain.model.user.search


data class UserSearchInfo (
    val userId: Long,
    val nickname: String,
    val profile: UserProfileInfo,
)
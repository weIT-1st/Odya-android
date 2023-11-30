package com.weit.data.model.user.search

import androidx.room.Embedded

data class UserSearchProfile(
    val profileUrl: String,
    @Embedded
    val profileColor: UserSearchProfileColor,
)

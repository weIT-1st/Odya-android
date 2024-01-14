package com.weit.data.model.user.search

import androidx.room.Entity

data class UserSearchProfileColor(
    val colorHex: String,
    val red: Int,
    val green: Int,
    val blue: Int,
)

package com.weit.data.model.user.search

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersearch")
data class UserSearch(
    @PrimaryKey
    val userId: Long,
    val nickname: String,
    @Embedded
    val profile: UserSearchProfile,
    val time: Long,
)

package com.weit.domain.model.user

data class UserByNickname(
    val size: Int = 10,
    val lastId: String?,
    val nickname: String,
)

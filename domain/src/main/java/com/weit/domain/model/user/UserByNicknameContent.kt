package com.weit.domain.model.user

data class UserByNicknameContent(
    val hasNext: Boolean,
    val content: List<UserContent>,
)

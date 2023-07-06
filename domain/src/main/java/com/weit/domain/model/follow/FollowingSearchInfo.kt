package com.weit.domain.model.follow

data class FollowingSearchInfo(
    val userId: Long,
    val page: Int,
    val size: Int,
    val sortType: String
)

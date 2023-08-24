package com.weit.domain.model.follow

data class FollowerSearchInfo(
    val userId: Long,
    val page: Int,
    val size: Int,
    val sortType: String = "LATEST",
)

package com.weit.domain.model.community.comment

data class CommentInfo(
    val communityId: Long,
    val size: Int? = null,
    val lastId:Long? = null,
)
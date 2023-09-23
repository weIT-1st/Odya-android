package com.weit.domain.model.community.comment

data class CommunityCommentInfo(
    val communityId: Long,
    val size: Int,
    val lastId:Long? = null,
)
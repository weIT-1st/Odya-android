package com.weit.domain.model.community


interface CommunityMainContent {
    val communityId: Long
    val placeId: String?
    val communityMainImageUrl: String
    val communityCommentCount: Int
    val communityLikeCount: Int
}

package com.weit.domain.model.community


interface CommunityMyActivityCommentContent {
    val communityId: Long
    val communityContent: String
    val communityMainImageUrl: String
    val updatedAt: String
    val writer: CommunityUser
    val communityCommentSimpleResponse: MyActivityCommentContent
}

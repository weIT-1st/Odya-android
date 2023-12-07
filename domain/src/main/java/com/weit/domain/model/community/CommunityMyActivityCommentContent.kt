package com.weit.domain.model.community

import java.time.LocalDateTime


interface CommunityMyActivityCommentContent {
    val communityId: Long
    val communityContent: String
    val communityMainImageUrl: String
    val updatedAt: LocalDateTime
    val writer: CommunityUser
    val communityCommentSimpleResponse: MyActivityCommentContent
}

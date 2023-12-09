package com.weit.domain.model.community


import com.weit.domain.model.topic.TopicDetail
import java.time.LocalDateTime

interface CommunityContent {
    val communityId: Long
    val content: String
    val visibility: String
    val placeId: String?
    val writer: CommunityUser
    val isWriter: Boolean
    val travelJournal: CommunityTravelJournal?
    val topic: TopicDetail?
    val communityContentImages: List<CommunityContentImage>
    val communityCommentCount: Int
    val communityLikeCount: Int
    val isUserLiked: Boolean
    val createdDate: LocalDateTime
}

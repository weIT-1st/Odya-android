package com.weit.domain.model.community


import com.weit.domain.model.topic.TopicDetail

interface CommunityContent {
    val communityId: Long
    val content: String
    val visibility: String
    val placeId: String
    val travelJournal: TravelJournal
    val topic: TopicDetail
    val communityContentImages: CommunityContentImage
    val communityCommentCount: Int
    val communityLikeCount: Int
    val isUserLiked: Boolean

}

package com.weit.domain.model.community

import java.time.LocalDateTime


interface CommunityMainContent {
    val communityId: Long
    val communityContent: String
    val placeId: String?
    val communityMainImageUrl: String
    val writer: CommunityUser
    val travelJournalSimpleResponse: CommunityTravelJournal?
    val communityCommentCount: Int
    val communityLikeCount: Int
    val isUserLiked: Boolean
    val createdDate: LocalDateTime
}

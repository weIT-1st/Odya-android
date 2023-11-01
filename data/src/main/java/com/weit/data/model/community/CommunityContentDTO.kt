package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityContentImage
import com.weit.domain.model.topic.TopicDetail

@JsonClass(generateAdapter = true)
data class CommunityContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "content") override val content: String,
    @field:Json(name = "visibility") override val visibility: String,
    @field:Json(name = "placeId") override val placeId: String,
    @field:Json(name = "travelJournal") override val travelJournal: TravelJournalDTO,
    @field:Json(name = "topic") override val topic: TopicDetail,
    @field:Json(name = "communityContentImages") override val communityContentImages: CommunityContentImage,
    @field:Json(name = "communityCommentCount") override val communityCommentCount: Int,
    @field:Json(name = "communityLikeCount") override val communityLikeCount: Int,
    @field:Json(name = "isUserLiked") override val isUserLiked: Boolean,
) : CommunityContent

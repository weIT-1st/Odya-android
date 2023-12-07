package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.util.StringToLocalDateTime
import com.weit.domain.model.community.CommunityContent
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CommunityContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "content") override val content: String,
    @field:Json(name = "visibility") override val visibility: String,
    @field:Json(name = "placeId") override val placeId: String?,
    @field:Json(name = "writer") override val writer: CommunityUserDTO,
    @field:Json(name = "travelJournal") override val travelJournal: TravelJournalDTO?,
    @field:Json(name = "topic") override val topic: CommunityTopicDTO?,
    @field:Json(name = "communityContentImages") override val communityContentImages: List<CommunityContentImageDTO>,
    @field:Json(name = "communityCommentCount") override val communityCommentCount: Int,
    @field:Json(name = "communityLikeCount") override val communityLikeCount: Int,
    @field:Json(name = "isUserLiked") override val isUserLiked: Boolean,
    @field:Json(name = "isWriter") override val isWriter: Boolean,
    @field:Json(name = "createdDate") @StringToLocalDateTime override val createdDate: LocalDateTime,
) : CommunityContent

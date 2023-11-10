package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.util.StringToLocalDateTime
import com.weit.domain.model.community.CommunityMainContent
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CommunityMainContentDTO(
    @field:Json(name = "communityId") override val communityId: Long,
    @field:Json(name = "communityContent") override val communityContent: String,
    @field:Json(name = "placeId") override val placeId: String?,
    @field:Json(name = "communityMainImageUrl") override val communityMainImageUrl: String,
    @field:Json(name = "writer") override val writer: CommunityUserDTO,
    @field:Json(name = "communityCommentCount") override val communityCommentCount: Int,
    @field:Json(name = "communityLikeCount") override val communityLikeCount: Int,
    @field:Json(name = "travelJournalSimpleResponse") override val travelJournalSimpleResponse: TravelJournalDTO?,
    @field:Json(name = "isUserLiked") override val isUserLiked: Boolean,
    @field:Json(name = "createdDate") @StringToLocalDateTime override val createdDate: LocalDateTime,
) : CommunityMainContent

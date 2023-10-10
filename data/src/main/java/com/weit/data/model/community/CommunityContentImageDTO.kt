package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.follow.FollowUserContentDTO
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityContentImage
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.TravelJournal
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.topic.TopicDetail
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class CommunityContentImageDTO(
    @field:Json(name = "communityContentImageId") override val communityContentImageId: Long,
    @field:Json(name = "imageUrl") override val imageUrl: String,
) : CommunityContentImage

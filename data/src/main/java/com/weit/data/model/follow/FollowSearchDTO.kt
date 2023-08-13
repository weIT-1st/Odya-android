package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.follow.FollowSearchDetail

@JsonClass(generateAdapter = true)
data class FollowSearchDTO(
    @field:Json(name = "hasNext") override val hasNext: Boolean,
    @field:Json(name = "content") override val content: List<FollowUserContentDTO>,
) : FollowSearchDetail

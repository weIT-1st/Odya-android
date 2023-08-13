package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.follow.FollowNumDetail

@JsonClass(generateAdapter = true)
data class FollowNumDTO(
    @field:Json(name = "followingCount") override val followingCount: Long,
    @field:Json(name = "followerCount") override val followerCount: Long,
) : FollowNumDetail

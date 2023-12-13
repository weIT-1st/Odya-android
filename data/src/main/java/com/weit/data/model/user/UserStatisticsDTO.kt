package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.UserStatistics

@JsonClass(generateAdapter = true)
data class UserStatisticsDTO(
    @field:Json(name = "travelJournalCount") override val travelJournalCount: Int,
    @field:Json(name = "travelPlaceCount") override val travelPlaceCount: Int,
    @field:Json(name = "followingsCount") override val followingsCount: Int,
    @field:Json(name = "followersCount") override val followersCount: Int,
    @field:Json(name = "odyaCount") override val odyaCount: Int,
    ) : UserStatistics

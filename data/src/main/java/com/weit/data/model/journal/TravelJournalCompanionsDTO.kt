package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalCompanionsDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileUrl") val profileUrl: String,
    @field:Json(name = "isRegistered") val isRegistered: Boolean,
)

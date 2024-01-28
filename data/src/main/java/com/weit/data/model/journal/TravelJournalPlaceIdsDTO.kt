package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO

@JsonClass(generateAdapter = true)
data class TravelJournalPlaceIdsDTO(
    @field:Json(name = "placeId") val placeId: String,
)

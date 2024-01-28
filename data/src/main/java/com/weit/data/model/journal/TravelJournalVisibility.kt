package com.weit.data.model.journal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelJournalVisibility(
    @field:Json(name = "visibility") val visibility: String,
)

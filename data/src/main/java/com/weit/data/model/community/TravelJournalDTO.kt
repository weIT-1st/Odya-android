package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.community.CommunityTravelJournal

@JsonClass(generateAdapter = true)
data class TravelJournalDTO(
    @field:Json(name = "travelJournalId") override val travelJournalId: Long,
    @field:Json(name = "title") override val title: String,
    @field:Json(name = "mainImageUrl") override val mainImageUrl: String?,
) : CommunityTravelJournal

package com.weit.presentation.model.post.travellog

import com.weit.domain.model.place.PlacePrediction
import java.time.LocalDate
import java.util.UUID

data class DailyTravelLog(
    val id: UUID = UUID.randomUUID(),
    val day: Int,
    val date: LocalDate? = null,
    var contents: String = "",
    val images: List<String> = emptyList(),
    val place: PlacePrediction? = null,
)

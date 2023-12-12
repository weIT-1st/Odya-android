package com.weit.presentation.model.post.travellog

import java.time.LocalDate

data class TravelPeriod(
    val start: LocalDate = LocalDate.now(),
    val end: LocalDate = LocalDate.now(),
)

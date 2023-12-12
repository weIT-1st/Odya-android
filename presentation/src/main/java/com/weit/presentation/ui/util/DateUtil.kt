package com.weit.presentation.ui.util

import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toMillis(): Long {
    return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

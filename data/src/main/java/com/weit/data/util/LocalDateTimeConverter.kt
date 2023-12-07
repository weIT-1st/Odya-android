package com.weit.data.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Retention
@JsonQualifier
annotation class StringToLocalDateTime
class LocalDateTimeConverter {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @ToJson
    fun toJson(@StringToLocalDateTime localDate: LocalDateTime): String {
        return dateFormat.format(localDate)
    }

    @FromJson
    @StringToLocalDateTime
    fun fromJson(source: String): LocalDateTime {
        return LocalDateTime.parse(source, dateFormat)
    }


}
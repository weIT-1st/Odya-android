package com.weit.data.model.map

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatLngResult(

    @field:Json(name = "lat")val lat: Double?,
    @field:Json(name = "lng")val lng: Double?,

)

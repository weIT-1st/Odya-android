package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PlaceEditorialSummary(
    @field:Json(name = "language")val language: String?,
    @field:Json(name = "overview")val overview: String?)

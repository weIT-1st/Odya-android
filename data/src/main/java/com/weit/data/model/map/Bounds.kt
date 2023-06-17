package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Bounds(
    @field:Json(name = "northeast")val northeast: LatLng,
    @field:Json(name = "southwest")val southwest: LatLng)

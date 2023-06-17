package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Geometry(

    @field:Json(name = "location")val location: LatLngResult,
    @field:Json(name = "location_type")val locationType: String,
    @field:Json(name = "viewport")val viewport: Bounds,


)
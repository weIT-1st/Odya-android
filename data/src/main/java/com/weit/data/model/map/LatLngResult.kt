package com.weit.data.model.map

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class LatLngResult(

    @field:Json(name = "lat")val lat: Double?,
    @field:Json(name = "lng")val lng: Double?,


)
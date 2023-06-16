package com.weit.presentation.model.map

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Geometry : Serializable {

    var bounds: Bounds? = null

    var location: LatLng? = null

    var locationType: LocationType? = null

    var viewport: Bounds? = null

    override fun toString(): String {
        return String.format(
            "[Geometry: %s (%s) bounds=%s, viewport=%s]", location, locationType, bounds, viewport)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
package com.weit.presentation.model.map

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Bounds : Serializable {
    var northeast: LatLng? = null
    var southwest: LatLng? = null
    override fun toString(): String {
        return String.format("[%s, %s]", northeast, southwest)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
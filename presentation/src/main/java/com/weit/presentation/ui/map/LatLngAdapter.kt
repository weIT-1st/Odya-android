package com.weit.presentation.ui.map

import com.google.android.gms.maps.model.LatLng
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

class LatLngAdapter : TypeAdapter<LatLng?>() {
    /**
     * <pre>{
     * "lat" : -33.8353684,
     * "lng" : 140.8527069
     * }
     *
     * {
     * "latitude": -33.865257570508334,
     * "longitude": 151.19287000481452
     * }</pre>
     */
    @Throws(IOException::class)
    override fun read(reader: JsonReader): LatLng? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        var lat = 0.0
        var lng = 0.0
        var hasLat = false
        var hasLng = false
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if ("lat" == name || "latitude" == name) {
                lat = reader.nextDouble()
                hasLat = true
            } else if ("lng" == name || "longitude" == name) {
                lng = reader.nextDouble()
                hasLng = true
            }
        }
        reader.endObject()
        return if (hasLat && hasLng) {
            LatLng(lat, lng)
        } else {
            null
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: LatLng?) {
        throw UnsupportedOperationException("Unimplemented method.")
    }
}
package com.weit.data.source

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.weit.data.db.CoordinateDatabase
import com.weit.data.model.Coordinate
import com.weit.data.repository.LocationRepositoryImpl
import com.weit.domain.model.CoordinateTimeInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    val context: Context
) {
    companion object {
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000  // 위치 업데이트 간 최소 시간 (1초)
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1f  // 위치 업데이트 간 최소 거리 (1미터)
    }

    private val lastLocation: Flow<Location> = emptyFlow()

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }



   suspend fun requestLocationUpdates(): Flow<Location> = callbackFlow {

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
       awaitClose { locationManager.removeUpdates(locationListener) }
    }
}

package com.weit.data.repository

import com.weit.data.di.db.LocationDatabase
import com.weit.data.model.Location
import com.weit.domain.model.LocationInfo
import com.weit.domain.model.LocationTimeInfo
import com.weit.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton


class LocationRepositoryImpl @Inject constructor(
    private val db: LocationDatabase,
) : LocationRepository {
    
    override suspend fun insertLocation(location: LocationInfo) {
        db.locationDao().insertLocation(location.toLocationInfo())
    }

    override suspend fun deleteLocation(locationId: Long) {
        db.locationDao().deleteLocation(locationId)
    }

    override suspend fun getLocationsInfo(timeInfo : LocationTimeInfo): List<LocationInfo> {
        val result = db.locationDao().getLocations(timeInfo.startTime, timeInfo.endTime)
        return result.map {
            LocationInfo(it.time,it.lat,it.lng)
        }
      }

    private fun LocationInfo.toLocationInfo(): Location =
        Location(
            id = null,
            time = time,
            lat = lat,
            lng = lng
        )
}

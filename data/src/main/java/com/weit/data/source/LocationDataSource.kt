package com.weit.data.source

import com.weit.data.di.db.LocationDatabase
import com.weit.data.model.Location
import com.weit.domain.model.LocationTimeInfo
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    private val db: LocationDatabase,
) {

    suspend fun insertLocation(location: Location) {
        db.locationDao().insertLocation(location)
    }

    suspend fun deleteLocation(locationId: Long) {
        db.locationDao().deleteLocation(locationId)
    }

    suspend fun getLocations(timeInfo: LocationTimeInfo): List<Location> {
        return db.locationDao().getLocations(timeInfo.startTime, timeInfo.endTime)
    }
}

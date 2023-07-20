package com.weit.data.repository.location

import com.weit.data.di.db.LocationDatabase
import com.weit.data.model.Location
import com.weit.data.source.LocationDataSource
import com.weit.data.source.PlaceDateSource
import com.weit.domain.model.LocationInfo
import com.weit.domain.model.LocationTimeInfo
import com.weit.domain.repository.LocationRepository
import javax.inject.Inject


class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource,
) : LocationRepository {
    
    override suspend fun insertLocation(location: LocationInfo) {
       dataSource.insertLocation(location.toLocation())
    }

    override suspend fun deleteLocation(locationId: Long) {
        dataSource.deleteLocation(locationId)
    }

    override suspend fun getLocationsInfo(timeInfo : LocationTimeInfo): List<LocationInfo> {
        val result = dataSource.getLocations(timeInfo)
        return result.map {
            LocationInfo(it.time,it.lat,it.lng)
        }
      }

    private fun LocationInfo.toLocation(): Location =
        Location(
            id = 0,
            time = time,
            lat = lat,
            lng = lng
        )
}

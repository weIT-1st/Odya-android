package com.weit.domain.repository

import com.weit.domain.model.LocationInfo
import com.weit.domain.model.LocationTimeInfo

interface LocationRepository {
    suspend fun insertLocation(location: LocationInfo)

    suspend fun deleteLocation(locationId: Long)

    suspend fun getLocationsInfo(timeInfo : LocationTimeInfo): List<LocationInfo>
}

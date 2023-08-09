package com.weit.data.repository.coordinate

import com.weit.data.model.Coordinate
import com.weit.data.source.CoordinateDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.repository.CoordinateRepository
import javax.inject.Inject

class CoordinateRepositoryImpl @Inject constructor(
    private val dataSource: CoordinateDataSource,
) : CoordinateRepository {

    override suspend fun insertCoordinate(location: CoordinateInfo) {
        dataSource.insertCoordinate(location.toLocation())
    }

    override suspend fun deleteCoordinate(locationId: Long) {
        dataSource.deleteCoordinate(locationId)
    }

    override suspend fun getCoordinatesInfo(timeInfo: CoordinateTimeInfo): List<CoordinateInfo> {
        val result = dataSource.getCoordinates(timeInfo)
        return result.map {
            CoordinateInfo(it.lat, it.lng)
        }
    }

    private fun CoordinateInfo.toLocation(): Coordinate =
        Coordinate(
            time = System.currentTimeMillis(),
            lat = lat,
            lng = lng,
        )
}

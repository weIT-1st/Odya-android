package com.weit.data.repository.coordinate

import com.weit.data.model.coordinate.Coordinate
import com.weit.data.source.CoordinateDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoordinateRepositoryImpl @Inject constructor(
    private val dataSource: CoordinateDataSource,
) : CoordinateRepository {

    override suspend fun insertCoordinate(coordinate: CoordinateInfo) {
        dataSource.insertCoordinate(coordinate.toCoordinate())
    }

    override suspend fun deleteCoordinate(coordinateId: Long) {
        dataSource.deleteCoordinate(coordinateId)
    }

    override suspend fun getCoordinatesInfo(timeInfo: CoordinateTimeInfo): List<CoordinateInfo> {
        val result = dataSource.getCoordinates(timeInfo)
        return result.map {
            CoordinateInfo(it.lat, it.lng)
        }
    }

    private fun CoordinateInfo.toCoordinate(): Coordinate =
        Coordinate(
            time = System.currentTimeMillis(),
            lat = lat,
            lng = lng,
        )
}

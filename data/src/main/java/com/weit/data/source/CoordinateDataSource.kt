package com.weit.data.source

import com.weit.data.db.CoordinateDatabase
import com.weit.data.model.Coordinate
import com.weit.domain.model.CoordinateTimeInfo
import javax.inject.Inject

class CoordinateDataSource @Inject constructor(
    private val db: CoordinateDatabase,
) {

    suspend fun insertCoordinate(coordinate: Coordinate) {
        db.coordinateDao().insertCoordinate(coordinate)
    }

    suspend fun deleteCoordinate(coordinateId: Long) {
        db.coordinateDao().deleteCoordinate(coordinateId)
    }

    suspend fun getCoordinates(timeInfo: CoordinateTimeInfo): List<Coordinate> {
        return db.coordinateDao().getCoordinates(timeInfo.startTime, timeInfo.endTime)
    }
}

package com.weit.domain.repository

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo

interface CoordinateRepository {
    suspend fun insertCoordinate(coordinate: CoordinateInfo)

    suspend fun deleteCoordinate(Id: Long)

    suspend fun getCoordinatesInfo(timeInfo: CoordinateTimeInfo): List<CoordinateInfo>
}

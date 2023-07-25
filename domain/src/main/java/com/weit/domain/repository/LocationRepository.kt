package com.weit.domain.repository

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurCoordinate(): Result<Flow<CoordinateInfo>>
}

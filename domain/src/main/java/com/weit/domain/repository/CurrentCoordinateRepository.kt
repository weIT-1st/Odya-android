package com.weit.domain.repository

import com.weit.domain.model.CoordinateInfo
import kotlinx.coroutines.flow.Flow

interface CurrentCoordinateRepository {

    suspend fun getCurCoordinate(): Result<Flow<CoordinateInfo>>
}

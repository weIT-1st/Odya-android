package com.weit.data.repository.coordinate

import android.Manifest
import android.location.Location
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.R
import com.weit.data.model.Coordinate
import com.weit.data.source.CoordinateDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.model.exception.RequestDeniedException
import com.weit.domain.repository.CoordinateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun getCurCoordinate(): Result<Flow<CoordinateInfo>> {
        val result = getLocationPermissionResult()
        return if (result.isGranted) {
            val coordinate = dataSource.requestLocationUpdates().map {
                it.toCoordinateInfo()
            }
            Result.success(coordinate)
        } else {
            Result.failure(RequestDeniedException(result.deniedPermissions.first()))
        }
    }

    private fun Location.toCoordinateInfo(): CoordinateInfo =
        CoordinateInfo(
            lat = latitude.toFloat(),
            lng = longitude.toFloat(),
        )

    private suspend fun getLocationPermissionResult(): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage(R.string.location_permission_denied)
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .check()
    }

    private fun CoordinateInfo.toLocation(): Coordinate =
        Coordinate(
            time = System.currentTimeMillis(),
            lat = lat,
            lng = lng,
        )
}

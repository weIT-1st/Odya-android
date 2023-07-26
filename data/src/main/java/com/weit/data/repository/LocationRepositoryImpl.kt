package com.weit.data.repository


import android.Manifest
import android.location.Location
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.R
import com.weit.data.source.LocationDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.exception.RequestDeniedException
import com.weit.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource,
) : LocationRepository {


    override suspend fun getCurCoordinate(): Result<Flow<CoordinateInfo>> {
        val result = getLocationPermissionResult()
        return if (result.isGranted) {
            val coordinate = dataSource.requestLocationUpdates().map {
                it.toCoordinateInfo()
            }
            Result.success(coordinate)
        }else {
            Result.failure(RequestDeniedException(result.deniedPermissions.first()))
        }
    }

    private fun Location.toCoordinateInfo(): CoordinateInfo =
        CoordinateInfo(
            lat = latitude.toFloat(),
            lng = longitude.toFloat(),
        )

    private suspend fun getLocationPermissionResult(): TedPermissionResult {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        return TedPermission.create()
            .setDeniedMessage(R.string.location_permission_denied)
            .setPermissions(permission)
            .setPermissions(permission2)
            .check()
    }

}

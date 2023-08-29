package com.weit.data.repository.setting

import android.Manifest
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.R
import com.weit.data.source.SettingDataSource
import com.weit.domain.model.exception.RequestDeniedException
import com.weit.domain.repository.setting.SettingRepository
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataSource: SettingDataSource,
) : SettingRepository {
    override fun verifyIgnoringBatteryOptimization() {
        return dataSource.setIgnoringBatteryOptimization()
    }

    override suspend fun verifyNotificationSetting(): Result<Unit> {
        val result = getNotificationPermissionResult()
        return if (result.isGranted) {
            Result.success(Unit)
        } else {
            Result.failure(RequestDeniedException(result.deniedPermissions.first()))
        }
    }

    override suspend fun verifyLocationPermission(): Result<Unit> {
        val result = getLocationPermissionResult()
        return if (result.isGranted) {
            val backgroundPermission = getBackgroundLocationPermissionResult()
            if (backgroundPermission.isGranted) {
                Result.success(Unit)
            } else {
                Result.failure(RequestDeniedException(result.deniedPermissions.first()))
            }
        } else {
            Result.failure(RequestDeniedException(result.deniedPermissions.first()))
        }
    }

    private suspend fun getLocationPermissionResult(): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage(R.string.location_permission_denied)
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .check()
    }

    private suspend fun getBackgroundLocationPermissionResult(): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage(R.string.background_location_permission_denied)
            .setPermissions(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
            .check()
    }

    private suspend fun getNotificationPermissionResult(): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage(R.string.notification_permission_denied)
            .setPermissions(
                Manifest.permission.POST_NOTIFICATIONS,
            )
            .check()
    }
}

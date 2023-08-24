package com.weit.domain.repository.setting

interface SettingRepository {
    fun verifyIgnoringBatteryOptimization()
    suspend fun verifyNotificationSetting(): Result<Unit>
    suspend fun verifyLocationPermission(): Result<Unit>
}

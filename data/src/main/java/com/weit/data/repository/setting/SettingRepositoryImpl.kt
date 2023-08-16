package com.weit.data.repository.setting

import com.weit.data.source.SettingDataSource
import com.weit.domain.repository.setting.SettingRepository
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataSource: SettingDataSource,
) : SettingRepository {
    override fun verifyIgnoringBatteryOptimization() {
        return dataSource.setIgnoringBatteryOptimization()
    }

    override fun verifyNotificationSetting() {
        return dataSource.setNotification()
    }
}

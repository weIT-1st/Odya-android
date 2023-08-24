package com.weit.domain.usecase.setting

import com.weit.domain.repository.setting.SettingRepository
import javax.inject.Inject

class VerifyLocationPermissionUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke() = settingRepository.verifyLocationPermission()
}

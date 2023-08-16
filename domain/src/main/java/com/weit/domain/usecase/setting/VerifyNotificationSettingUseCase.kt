package com.weit.domain.usecase.setting

import com.weit.domain.repository.setting.SettingRepository
import javax.inject.Inject

class VerifyNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    operator fun invoke() = settingRepository.verifyNotificationSetting()
}

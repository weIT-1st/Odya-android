package com.weit.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import com.weit.domain.usecase.setting.VerifyLocationPermissionUseCase
import com.weit.domain.usecase.setting.VerifyNotificationSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    fun verifyIgnoringBatteryOptimization(
        verifyIgnoringBatteryOptimizationUseCase: VerifyIgnoringBatteryOptimizationUseCase,
    ) {
        verifyIgnoringBatteryOptimizationUseCase()
    }

    fun verifyNotificationSetting(
        verifyNotificationSettingUseCase: VerifyNotificationSettingUseCase,
    ) {
        viewModelScope.launch {
            verifyNotificationSettingUseCase()
        }
    }

    fun verifyLocationPermission(
        verifyLocationPermissionUseCase: VerifyLocationPermissionUseCase,
    ) {
        viewModelScope.launch {
            verifyLocationPermissionUseCase()
        }
    }
}

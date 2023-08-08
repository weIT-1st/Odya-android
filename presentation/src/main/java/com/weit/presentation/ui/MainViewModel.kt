package com.weit.presentation.ui

import androidx.lifecycle.ViewModel
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

    fun verifyIgnoringBatteryOptimization(
        verifyIgnoringBatteryOptimizationUseCase: VerifyIgnoringBatteryOptimizationUseCase){
        verifyIgnoringBatteryOptimizationUseCase()
    }

}
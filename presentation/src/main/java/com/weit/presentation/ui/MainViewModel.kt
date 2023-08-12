package com.weit.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.usecase.setting.VerifyIgnoringBatteryOptimizationUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.domain.usecase.user.SetUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val setUserIdUseCase: SetUserIdUseCase,
) : ViewModel() {

    init {
        setUserId()
    }

    private fun setUserId() {
        // TODO 자신의 정보를 가져오는데 실패 했을 경우 에러 핸들링 (재시도 UI 띄우기 같은거)
        // TODO 지금은 Id만 저장하는데 유저 정보 객체를 Proto DataStore로 저장하기
        viewModelScope.launch {
            val user = getUserUseCase().getOrThrow()
            Logger.t("MainTest").i("$user")
            setUserIdUseCase(userId = user.userID)
        }
    }

    fun verifyIgnoringBatteryOptimization(
        verifyIgnoringBatteryOptimizationUseCase: VerifyIgnoringBatteryOptimizationUseCase,
    ) {
        verifyIgnoringBatteryOptimizationUseCase()
    }
}

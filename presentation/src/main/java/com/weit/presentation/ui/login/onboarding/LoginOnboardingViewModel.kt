package com.weit.presentation.ui.login.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.R
import com.weit.presentation.model.LoginOnboardingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginOnboardingViewModel @Inject constructor(

): ViewModel() {
    private val _loginOnboardingContents = MutableStateFlow<List<LoginOnboardingModel>>(emptyList())
    val loginOnboardingContents : StateFlow<List<LoginOnboardingModel>> get() = _loginOnboardingContents

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> get() = _currentPage

    init {
        settingLoginOnboardingContents()
    }

    private fun settingLoginOnboardingContents(){
        viewModelScope.launch {
            val odya = LoginOnboardingModel(
                main = R.string.login_onboarding_odya_main,
                detail = R.string.login_onboarding_route_detail,
                image = R.drawable.image_onboarding_odya,
            )
            val journal = LoginOnboardingModel(
                main = R.string.login_onboarding_journal_main,
                detail = R.string.login_onboarding_journal_detail,
                image = R.drawable.image_onboarding_journal,
            )
            val routine = LoginOnboardingModel(
                main = R.string.login_onboarding_route_main,
                detail = R.string.login_onboarding_route_detail,
                image = R.drawable.image_onboarding_travel_routine,
            )

            val contents = listOf(odya, journal, routine)

            _loginOnboardingContents.emit(contents)
        }
    }

    fun changeCurrentPage(position: Int){
        viewModelScope.launch {
            _currentPage.emit(position)
        }
    }
}

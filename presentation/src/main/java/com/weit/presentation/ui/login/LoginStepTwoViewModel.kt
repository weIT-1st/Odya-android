package com.weit.presentation.ui.login

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginStepTwoViewModel@Inject constructor(
): ViewModel() {

    private lateinit var birth: LocalDate

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        birth = LocalDate.of(year, month + 1, day)
    }
}
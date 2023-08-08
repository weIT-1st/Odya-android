package com.weit.presentation.ui.login.genderbirth

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import com.weit.presentation.model.GenderType
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginGenderBirthViewModel @Inject constructor(
): ViewModel() {
    private var gender = GenderType.IDLE
    private lateinit var birth: LocalDate

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        birth = LocalDate.of(year, month + 1, day)
    }

    @MainThread
    fun setGender(genderType: GenderType) {
        gender = genderType
    }

    private suspend fun checkInputGenderBirth(){
        if (gender == GenderType.IDLE){
            _event.emit(Event.GenderNotSelected)
        }

        if (::birth.isInitialized.not()){
            _event.emit(Event.BirthNotSelected)
        }
    }

    sealed class Event{
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
    }
}
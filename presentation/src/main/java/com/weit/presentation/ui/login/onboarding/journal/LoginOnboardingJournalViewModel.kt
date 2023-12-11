package com.weit.presentation.ui.login.onboarding.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginOnboardingJournalViewModel @Inject constructor(

): ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun goNextStep(){
        viewModelScope.launch {
            _event.emit(Event.GoNextStep)
        }
    }

    sealed class Event{
        object GoNextStep : Event()
    }
}

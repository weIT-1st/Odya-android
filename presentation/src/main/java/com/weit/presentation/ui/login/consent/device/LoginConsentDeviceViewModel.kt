package com.weit.presentation.ui.login.consent.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.ui.util.MutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginConsentDeviceViewModel @Inject constructor(

): ViewModel() {
    private val _event = MutableEventFlow<Event>()
    val event = _event

    fun goConsentPrivacy(){
        viewModelScope.launch {
            _event.emit(Event.ConsentPrivacyBottomSheetUp)
        }
    }

    sealed class Event(){
        object ConsentPrivacyBottomSheetUp: Event()
    }
}

package com.weit.presentation.ui.login.consent

import androidx.annotation.MainThread
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginConsentViewModel @Inject constructor(

) : ViewModel(){

    private var _isConsentApp =  MutableStateFlow(false)
    val isConsentApp: StateFlow<Boolean> get() = _isConsentApp

    private var _isConsentPrivacy = MutableStateFlow(false)
    val isConsentPrivacy: StateFlow<Boolean> get() = _isConsentPrivacy

    val isConsentAll: StateFlow<Boolean> get() = isConsentApp.combine(isConsentPrivacy){ app, privacy ->
        app && privacy
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)


    @MainThread
    fun checkConsentApp(){
        viewModelScope.launch(){
            if (isConsentApp.value){_isConsentApp.emit(!isConsentApp.value)}
        }
    }

    @MainThread
    fun checkConsentPrivacy(){
        viewModelScope.launch {
            if(isConsentPrivacy.value){_isConsentPrivacy.emit(!isConsentPrivacy.value)}
        }
    }

    @MainThread
    fun checkConsentAll(){
        viewModelScope.launch {
            if (isConsentAll.value) {
                _isConsentApp.emit(!isConsentAll.value)
                _isConsentPrivacy.emit(!isConsentAll.value)
            }
        }
    }

}
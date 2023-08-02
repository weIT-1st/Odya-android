package com.weit.presentation.ui.login

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginMainViewModel @Inject constructor() : ViewModel(){

    private val _position = MutableEventFlow<Int>(0)
    val position = _position.asEventFlow()

    private fun btnOnOff(btnLoginGoBackSteop : AppCompatButton, btnLoginGoNextSteop: AppCompatButton){
        viewModelScope.launch {
        }
    }
}
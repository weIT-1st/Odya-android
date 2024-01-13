package com.weit.presentation.ui.profile.menu

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.user.UpdateProfileUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileMenuViewModel @Inject constructor(
   private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onUpdateProfileImage(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val image = pickImageUseCase()
            viewModelScope.launch {
                val result = updateProfileUseCase(image.first())
                if (result.isSuccess) {
                    _event.emit(Event.OnChangeProfileImageSuccess(image.first()))
                } else {
                    // TODO 에러 처리
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun onUpdateProfileImageNone() {
        viewModelScope.launch {
            val result = updateProfileUseCase(null)
                if (result.isSuccess) {
                    _event.emit(Event.OnChangeProfileNoneSuccess)
                } else {

                }
            }
    }

    sealed class Event {
        data class OnChangeProfileImageSuccess(
            val image: String,
        ) : Event()
        object OnChangeProfileNoneSuccess : Event()
    }
}

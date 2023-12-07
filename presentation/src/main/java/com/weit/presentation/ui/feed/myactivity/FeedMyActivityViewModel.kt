package com.weit.presentation.ui.feed.myactivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.follow.ExperiencedFriendContent
import com.weit.domain.model.user.User
import com.weit.domain.usecase.follow.GetExperiencedFriendUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlaceImageUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedMyActivityViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,

    ) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> get() = _user
    init {
        viewModelScope.launch {
            getUserUseCase().onSuccess {
                _user.value = it
            }
        }
    }
}

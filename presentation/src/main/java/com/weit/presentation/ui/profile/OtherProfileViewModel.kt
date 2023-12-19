package com.weit.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.user.DeleteUserUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.domain.usecase.user.GetUserSearchUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
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

class OtherProfileViewModel @AssistedInject constructor(
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    private val getUserSearchUseCase: GetUserSearchUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    @Assisted private val userName: String,

    ) : ViewModel() {


//    @AssistedFactory
//    interface FeedDetailFactory {
//        fun create(feedId: Long): FeedDetailViewModel
//    }
//    val user = MutableStateFlow<User?>(null)

    private val _event = MutableEventFlow<OtherProfileViewModel.Event>()
    val event = _event.asEventFlow()

    init {
        getUserInfo()
        getUserStatistics()

    }

    private fun getUserInfo(){
        //팔로우 여부

    }


    private fun getUserStatistics() {
//        viewModelScope.launch {
//            val result = getUserStatisticsUseCase(user.userId)
//                if (result.isSuccess) {
//                    _event.emit(Event.GetUserStatisticsSuccess(result.getOrThrow(),user))
//                } else {
//                    handleError(result.exceptionOrNull() ?: UnKnownException())
//                }
//            }
        }


    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {

        data class GetUserStatisticsSuccess(
            val statistics : UserStatistics,
            val user: User
        ) : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }
}


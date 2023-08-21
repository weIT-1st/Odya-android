package com.weit.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.exception.topic.NotHavePermissionException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.topic.RegisterFavoriteTopicUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerFavoriteTopicUseCase: RegisterFavoriteTopicUseCase,
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
)  : ViewModel() {

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private val _favoriteTopics = MutableEventFlow<List<TopicDetail>>()
    val favoriteTopics = _favoriteTopics.asEventFlow()

    init {
        //getTopicList()
//        addFavoriteTopic()
        getFavoriteTopicList()
    }
    private fun getTopicList() {
        viewModelScope.launch {
            val result = getTopicListUseCase()
            if (result.isSuccess) {
                val topic = result.getOrThrow()
                Logger.t("MainTest").i("$topic")
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun getFavoriteTopicList() {
        viewModelScope.launch {
            val result = getFavoriteTopicListUseCase()
            if (result.isSuccess) {
                val topic = result.getOrThrow()
                Logger.t("MainTest").i("$topic")
                _favoriteTopics.emit(topic)
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun addFavoriteTopic() {
        viewModelScope.launch {
            val result = registerFavoriteTopicUseCase(
                listOf(1,2),
            )
            if (result.isSuccess) {
                Logger.t("MainTest").i("성공!")
            } else {
                handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private suspend fun handleRegistrationError(error: Throwable) {
        when (error) {
            is NotExistTopicIdException -> _event.emit(Event.NotExistTopicIdException)
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is NotHavePermissionException -> _event.emit(Event.NotHavePermissionException)
            else -> _event.emit(Event.UnknownException)
        }
    }
    sealed class Event {
        object NotExistTopicIdException : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object UnknownException : Event()
    }
}

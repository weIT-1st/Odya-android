package com.weit.presentation.ui.login.input.topic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.topic.RegisterFavoriteTopicUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.topic.TopicChoiceInfo
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginTopicViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerFavoriteTopicUseCase: RegisterFavoriteTopicUseCase
) : ViewModel() {
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> get() = _nickname

    private val _topics = MutableStateFlow<List<TopicChoiceInfo>>(emptyList())
    val topics: StateFlow<List<TopicChoiceInfo>> get() = _topics

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getUserNickname()
        getTopics()
    }

    private fun getUserNickname() {
        viewModelScope.launch {
            val result = getUserUseCase()

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _nickname.emit(user.nickname)
            } else {
                Log.d("getUserNickname", "Get UserNickname fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun updateFavoriteTopics(topicId: Long) {
        viewModelScope.launch {
            val list = topics.value.toMutableList()
            val topic = list.find { it.topicId == topicId }

            if (topic != null) {
                list[list.indexOf(topic)] = TopicChoiceInfo(
                    topicId = topic.topicId,
                    topicWord = topic.topicWord,
                    isChoice = !topic.isChoice
                )

                _topics.emit(list)
            }
        }
    }

    fun registrationFavoriteTopics() {
        viewModelScope.launch {
            val list = topics.value.filter { it.isChoice }.map { it.topicId }

            if (list.size < MINIMUN_TOPIC_COUNT) {
                _event.emit(Event.NeedToMinimumFavoriteTopicCount)
            } else {
                val result = registerFavoriteTopicUseCase(list)

                if (result.isSuccess) {
                    _event.emit(Event.RegisterTopicSuccess)
                } else {
                    Log.d("registTopic", "Registration Topic fail : ${result.exceptionOrNull()}")
                }
            }
        }
    }

    private fun getTopics() {
        viewModelScope.launch {
            val result = getTopicListUseCase()

            if (result.isSuccess) {
                val list = result.getOrThrow()
                _topics.emit(list.map {
                    TopicChoiceInfo(
                        it.topicId,
                        it.topicWord
                    )
                })
            } else {
                Log.d("getTopicList", "Get TopicList fail : ${result.exceptionOrNull()}")
            }
        }
    }

    sealed class Event {
        object NeedToMinimumFavoriteTopicCount : Event()
        object RegisterTopicSuccess : Event()
    }

    companion object {
        const val MINIMUN_TOPIC_COUNT = 3
    }
}

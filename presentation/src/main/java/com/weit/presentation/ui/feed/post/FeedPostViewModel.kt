package com.weit.presentation.ui.feed.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.ui.feed.FeedViewModel
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class FeedPostViewModel @AssistedInject constructor(
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerCommunityUseCase: RegisterCommunityUseCase,
    @Assisted private val imageUris: List<String>,
    ): ViewModel() {
    private val _event = MutableEventFlow<FeedPostViewModel.Event>()
    val event = _event.asEventFlow()

    private val topicList: MutableList<TopicDetail> = mutableListOf()
    private var selectedTopicId :Long? = null
    val content = MutableStateFlow("")

    @AssistedFactory
    interface FeedPostFactory {
        fun create(imageUris: List<String>): FeedPostViewModel
    }

    init{
        getTopicList()
    }

     private fun getTopicList() {
        viewModelScope.launch {
            val result = getTopicListUseCase()
            if (result.isSuccess) {
                var topics = result.getOrThrow()
                topicList.addAll(topics)
                _event.emit(Event.initTopics(topics))
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun selectTopic(value: String){
        selectedTopicId = topicList.find { it.topicWord == value }?.topicId
    }

    fun registerCommunity(){
        viewModelScope.launch {

            val result = registerCommunityUseCase(
                CommunityRegistrationInfo(
                    content.value,
                    "PUBLIC",
                    null,
                    null,
                    selectedTopicId
                ),
                imageUris
            )
            if (result.isSuccess) {
                _event.emit(Event.FeedPostSuccess)
            } else {
//                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    sealed class Event {
        data class initTopics(
            val topics: List<TopicDetail>,
        ) : Event()

        object FeedPostSuccess : Event()
        object UnknownException : Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: FeedPostViewModel.FeedPostFactory,
            imageUris: List<String>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(imageUris) as T
            }
        }
    }
}

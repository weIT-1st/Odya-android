package com.weit.presentation.ui.feed.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.ui.feed.FeedViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class FeedPostViewModel @AssistedInject constructor(
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerCommunityUseCase: RegisterCommunityUseCase,
    @Assisted private val imageUris: List<String>,
    ): ViewModel() {
    private val _event = MutableEventFlow<FeedPostViewModel.Event>()
    val event = _event.asEventFlow()

//    private val _topicList = MutableStateFlow<List<TopicDetail>?>(null)
//    val topicList : StateFlow<List<TopicDetail>?> get() = _topicList

    private val _imageList = MutableStateFlow<List<String>?>(emptyList())
    val imageList : StateFlow<List<String>?> get() = _imageList

    private var selectedTopicId :Long? = null
    val content = MutableStateFlow("")

    private var topicList = CopyOnWriteArrayList<FeedTopic>()

    @AssistedFactory
    interface FeedPostFactory {
        fun create(imageUris: List<String>?): FeedPostViewModel
    }

    init{
        viewModelScope.launch {
            _imageList.emit(imageUris)
        }
        getTopicList()
    }


     private fun getTopicList() {
        viewModelScope.launch {
            val result = getTopicListUseCase()
            if (result.isSuccess) {
                val topics = result.getOrThrow().map{
                    FeedTopic(
                        it.topicId,it.topicWord,false
                    )
                }
                topicList.addAll(topics)
                _event.emit(Event.OnChangeTopics(topics))
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun updateTopicUI(topicId: Long) {
        viewModelScope.launch {
            val newTopics = topicList.map{
                if(it.topicId == topicId){
                    it.copy(isChecked = true)
                }else{
                    it.copy(isChecked = false)
                }
            }
            topicList.clear()
            topicList.addAll(newTopics)
            _event.emit(Event.OnChangeTopics(newTopics))
        }
    }

    fun selectTopic(topicId: Long){
        updateTopicUI(topicId)
        selectedTopicId = topicId
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
                _imageList.value ?: emptyList()
            )
            if (result.isSuccess) {
                _event.emit(Event.FeedPostSuccess)
            } else {
//                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun onUpdatePictures(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
            _imageList.emit(images.toList())
        }
    }

    sealed class Event {
        data class OnChangeTopics(
            val topics: List<FeedTopic>,
        ) : FeedPostViewModel.Event()
        object FeedPostSuccess : Event()
        object UnknownException : Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: FeedPostViewModel.FeedPostFactory,
            imageUris: List<String>?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(imageUris) as T
            }
        }
    }
}

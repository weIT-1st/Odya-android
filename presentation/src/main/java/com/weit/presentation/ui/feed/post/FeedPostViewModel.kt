package com.weit.presentation.ui.feed.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityUpdateInfo
import com.weit.domain.usecase.community.GetDetailCommunityUseCase
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.community.UpdateCommunityUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotPlaceDTO
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerViewModel
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
    private val getDetailCommunityUseCase: GetDetailCommunityUseCase,
    private val updateCommunityUseCase: UpdateCommunityUseCase,
    @Assisted private val imageUris: List<String>,
    @Assisted private val feedId: Long,
    ): ViewModel() {
    private val _event = MutableEventFlow<FeedPostViewModel.Event>()
    val event = _event.asEventFlow()

    private val _feed = MutableStateFlow<CommunityContent?>(null)
    val feed: StateFlow<CommunityContent?> get() = _feed

    private val _imageList = MutableStateFlow<List<String>>(emptyList())
    val imageList : StateFlow<List<String>> get() = _imageList

    private var selectedPlace :SelectLifeShotPlaceDTO? = null
    private var selectedTopicId :Long? = null
    private var selectedVisibility :Visibility = Visibility.PUBLIC

    val content = MutableStateFlow("")

    private val topicList = CopyOnWriteArrayList<FeedTopic>()
    private var feedState = feedRegister

    @AssistedFactory
    interface FeedPostFactory {
        fun create(imageUris: List<String>?,
                   feedId:Long): FeedPostViewModel
    }

    init{
        viewModelScope.launch {
            _imageList.emit(imageUris)
        }
        getTopicList()

        if(feedId>0){
            getFeed()
            feedState = feedUpdate
        }
    }

    private fun getFeed() {
        viewModelScope.launch {
            val result = getDetailCommunityUseCase(feedId)
            if (result.isSuccess) {
                val feed = result.getOrThrow()
                val uris = feed.communityContentImages.map{
                    it.imageUrl
                }
                _imageList.emit(uris)
                content.value = feed.content
                feed.topic?.topicId?.let { updateTopicUI(it) }
                selectedTopicId = feed.topic?.topicId
                selectedVisibility = Visibility.valueOf(feed.visibility)
                //TODO 장소
                _feed.emit(feed)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
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
            val newTopics = topicList.map { feedTopic ->
                feedTopic.copy(isChecked = feedTopic.topicId == topicId)
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

    fun selectVisibility(visibility: Visibility){
        selectedVisibility = visibility
    }

    fun registerCommunity() {
        viewModelScope.launch {
            when (feedState) {
                feedRegister -> {
                    val result = registerCommunityUseCase(
                        CommunityRegistrationInfo(
                            content.value,
                            selectedVisibility.name,
                            selectedPlace?.placeId,
                            null,
                            selectedTopicId
                        ),
                        _imageList.value?: emptyList()
                    )
                    if (result.isSuccess) {
                        _event.emit(Event.FeedPostSuccess)
                    } else {
                    }
                }

                feedUpdate -> {
                    val originalImageIds = feed.value?.communityContentImages?.map{ it.communityContentImageId } ?: emptyList()

                    val result = updateCommunityUseCase(
                        feedId,
                        CommunityUpdateInfo(
                            content.value,
                            selectedVisibility.name,
                            selectedPlace?.placeId,
                            null,
                            selectedTopicId,
                            originalImageIds),
                        _imageList.value?: emptyList()
                    )
                    if (result.isSuccess) {
                        _event.emit(Event.FeedUpdateSuccess)
                    } else {
//                handleError(result.exceptionOrNull() ?: UnKnownException())
                    }
                }
            }

        }
    }

    fun onUpdatePictures(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
            when (feedState) {
                feedUpdate -> _imageList.value = emptyList()
            }
            _imageList.emit(images)
        }
    }

    fun selectFeedPlace(place: SelectLifeShotPlaceDTO){
        viewModelScope.launch {
            selectedPlace = place
            _event.emit(Event.OnSelectPlaceCompleted(place.name))
        }
    }

    sealed class Event {
        data class OnChangeTopics(
            val topics: List<FeedTopic>,
        ) : FeedPostViewModel.Event()

        data class OnSelectPlaceCompleted(
            val placeName: String,
        ) : FeedPostViewModel.Event()
        object FeedPostSuccess : Event()
        object FeedUpdateSuccess : Event()

        object UnknownException : Event()
    }

    companion object {
        const val feedRegister = "register"
        const val feedUpdate = "update"
        fun provideFactory(
            assistedFactory: FeedPostViewModel.FeedPostFactory,
            imageUris: List<String>?,
            feedId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(imageUris,feedId) as T
            }
        }
    }
}

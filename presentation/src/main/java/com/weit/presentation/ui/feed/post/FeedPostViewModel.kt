package com.weit.presentation.ui.feed.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityUpdateInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.community.GetDetailCommunityUseCase
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.community.UpdateCommunityUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.feed.SelectTravelJournalDTO
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
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    @Assisted private val imageUris: List<String>,
    @Assisted private val feedId: Long,
    ): ViewModel() {
    private val _event = MutableEventFlow<FeedPostViewModel.Event>()
    val event = _event.asEventFlow()

    private val _feed = MutableStateFlow<CommunityContent?>(null)
    val feed: StateFlow<CommunityContent?> get() = _feed

    private val _imageList = MutableStateFlow<List<String>>(emptyList())
    val imageList : StateFlow<List<String>> get() = _imageList

    private val _topicList = MutableStateFlow<List<FeedTopic>>(emptyList())
    val topicList : StateFlow<List<FeedTopic>> get() = _topicList

    private val _placeName = MutableStateFlow<String>("")
    val placeName : StateFlow<String> get() = _placeName

    private val _journalTitle = MutableStateFlow<String?>(null)
    val journalTitle : StateFlow<String?> get() = _journalTitle

    private var selectedPlaceId :String? = null
    private var selectedTopicId :Long? = null
    private var selectedTravelJournalId :Long? = null
    private var selectedVisibility :Visibility = Visibility.PUBLIC

    val content = MutableStateFlow("")

//    private val topicList = CopyOnWriteArrayList<FeedTopic>()
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
                getPlaceName(feed.placeId)
                feed.travelJournal?.let {
                    selectedTravelJournalId = it.travelJournalId
                    _journalTitle.emit(it.title)
                }
                _feed.emit(feed)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun getPlaceName(placeId: String?){
        viewModelScope.launch {
            if(placeId.isNullOrEmpty().not()){
                val placeInfo = getPlaceDetailUseCase(placeId.toString())
                if (placeInfo.name.isNullOrBlank().not()) {
                    selectedPlaceId = placeId
                    _placeName.emit(placeInfo.name.toString())
                }
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
                _topicList.emit(topics)
                _event.emit(Event.OnChangeTopics(topics))
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun updateTopicUI(topicId: Long) {
        viewModelScope.launch {
            val newTopics = topicList.value.map { feedTopic ->
                feedTopic.copy(isChecked = feedTopic.topicId == topicId)
            }
            _topicList.emit(newTopics)
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
                            selectedPlaceId,
                            selectedTravelJournalId,
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
                            selectedPlaceId,
                            selectedTravelJournalId,
                            selectedTopicId,
                            originalImageIds),
                        _imageList.value?: emptyList() //현재 원래 이미지를 전부 지우고 새로 이미지를 선택했을 때 성공적으로 됨
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
            selectedPlaceId = place.placeId
            _placeName.emit(place.name)
        }
    }

    fun selectTravelJournal(journal: SelectTravelJournalDTO){
        viewModelScope.launch {
            selectedTravelJournalId = journal.travelJournalId
            _journalTitle.emit(journal.travelJournalTitle)
        }
    }

    fun onClickTravelJournalView(){
        viewModelScope.launch {
            _event.emit(Event.OnClickTravelJournalView(selectedTravelJournalId))
        }
    }

    sealed class Event {
        data class OnChangeTopics(
            val topics: List<FeedTopic>,
        ) : FeedPostViewModel.Event()

        data class OnClickTravelJournalView(
            val journalId: Long?,
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

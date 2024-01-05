package com.weit.presentation.ui.searchplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.follow.ExperiencedFriendContent
import com.weit.domain.usecase.follow.GetExperiencedFriendUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlaceImageUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchPlaceBottomSheetViewModel @AssistedInject constructor(
    private val getExperiencedFriendUseCase: GetExperiencedFriendUseCase,
    private val getPlaceImageUseCase: GetPlaceImageUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    @Assisted private val placeId: String,
) : ViewModel() {

    private val _experiencedFriendNum = MutableStateFlow(INIT_EXPERIENCED_FRIEND_COUNT)
    val experiencedFriendNum: StateFlow<Int> get() = _experiencedFriendNum

    private val _experiencedFriend = MutableStateFlow<List<ExperiencedFriendContent>>(emptyList())
    val experiencedFriend: StateFlow<List<ExperiencedFriendContent>> get() = _experiencedFriend

    private val _placeImage = MutableStateFlow<ByteArray?>(null)
    val placeImage: StateFlow<ByteArray?> get() = _placeImage

    private val _placeTitle = MutableStateFlow(DEFAULT_PLACE_TITLE)
    val placeTitle: StateFlow<String> get() = _placeTitle

    private val _placeAddress = MutableStateFlow(DEFAULT_PLACE_ADDRESS)
    val placeAddress: StateFlow<String> get() = _placeAddress

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): SearchPlaceBottomSheetViewModel
    }

    init {
        viewModelScope.launch {
            initExperiencedFriend()
            getPlaceInform()
        }
    }

    private suspend fun initExperiencedFriend() {
        // todo 친구수가 1~2명일떄 + 리사이클러뷰 아이템 간격 조절
        val result = getExperiencedFriendUseCase(placeId)
        if (result.isSuccess) {
            val info = result.getOrThrow()
            _event.emit(Event.GetExperiencedFriendSuccess)
            _experiencedFriendNum.emit(info.count)
            if (info.count != 0) {
                val friendSummary = info.followings
                    .slice(0 until DEFAULT_FRIENDS_SUMMARY_COUNT)

                _experiencedFriend.emit(friendSummary)
            }
        } else {
            handelError(result.exceptionOrNull() ?: UnknownError())
        }
    }

    private suspend fun getPlaceInform() {
        val placeImageByteArray = getPlaceImageUseCase(placeId)

        if (placeImageByteArray.isSuccess){
            _placeImage.emit(placeImageByteArray.getOrThrow())
        }

        val placeInform = getPlaceDetailUseCase(placeId)

        if (placeInform.name.isNullOrBlank().not()) {
            _placeTitle.emit(placeInform.name!!)
        }
        if (placeInform.address.isNullOrBlank().not()) {
            _placeAddress.emit(placeInform.address!!)
        }
    }

    private suspend fun handelError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {
        object GetExperiencedFriendSuccess : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }

    companion object {
        private const val INIT_EXPERIENCED_FRIEND_COUNT = 0
        private const val DEFAULT_FRIENDS_SUMMARY_COUNT = 2
        private const val DEFAULT_PLACE_TITLE = ""
        private const val DEFAULT_PLACE_ADDRESS = ""
       fun provideFactory(
            assistedFactory: PlaceIdFactory,
            placeId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeId) as T
            }
        }
    }
}

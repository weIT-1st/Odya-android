package com.weit.presentation.ui.main.place

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.follow.ExperiencedFriendContent
import com.weit.domain.usecase.bookmark.CreateJournalBookMarkUseCase
import com.weit.domain.usecase.favoritePlace.GetIsFavoritePlaceUseCase
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
    private val getIsFavoritePlaceUseCase: GetIsFavoritePlaceUseCase,
    @Assisted private val placeId: String,
) : ViewModel() {

    private val _experiencedFriendInfo = MutableStateFlow(ExperiencedFriendInfo())
    val experiencedFriendInfo: StateFlow<ExperiencedFriendInfo> get() = _experiencedFriendInfo

    private val _placeImage = MutableStateFlow<ByteArray?>(null)
    val placeImage: StateFlow<ByteArray?> get() = _placeImage

    private val _placeInfo = MutableStateFlow(PlaceInformation())
    val placeInfo: StateFlow<PlaceInformation> get() = _placeInfo

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): SearchPlaceBottomSheetViewModel
    }

    init {
        getPlaceImage()
        getPlaceInformation()
        getExperiencedFriend()
        getIsFavoritePlace()
    }

    private fun getExperiencedFriend() {
        viewModelScope.launch {
            val result = getExperiencedFriendUseCase(placeId)

            if (result.isSuccess) {
                val info = result.getOrThrow()

                val friendSummary = if (info.count > DEFAULT_FRIENDS_SUMMARY_COUNT) {
                    info.followings.slice(0 until DEFAULT_FRIENDS_SUMMARY_COUNT)
                } else {
                    info.followings
                }
                _experiencedFriendInfo.emit(ExperiencedFriendInfo(info.count, friendSummary))
            } else {
                // todo 에러처리
                Log.d("getExperiencedFriend", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getPlaceImage() {
        viewModelScope.launch {
            val placeImageByteArray = getPlaceImageUseCase(placeId)

            if (placeImageByteArray.isSuccess) {
                _placeImage.emit(placeImageByteArray.getOrThrow())
            }
        }
    }

    private fun getPlaceInformation() {
        viewModelScope.launch {
            val info = getPlaceDetailUseCase(placeId)
            _placeInfo.emit(PlaceInformation(info.name ?: DEFAULT_PLACE_TITLE, info.address ?: DEFAULT_PLACE_ADDRESS))
        }
    }

    private fun getIsFavoritePlace() {
        viewModelScope.launch {
            val result = getIsFavoritePlaceUseCase(placeId)

            if (result.isSuccess) {

            }
        }
    }

    data class ExperiencedFriendInfo (
        val count : Int = INIT_EXPERIENCED_FRIEND_COUNT,
        val summaryFriends : List<ExperiencedFriendContent> = emptyList()
    )

    data class PlaceInformation (
        val title : String = DEFAULT_PLACE_TITLE,
        val address : String = DEFAULT_PLACE_ADDRESS
    )

    companion object {
        private const val INIT_EXPERIENCED_FRIEND_COUNT = 0
        private const val DEFAULT_FRIENDS_SUMMARY_COUNT = 3
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

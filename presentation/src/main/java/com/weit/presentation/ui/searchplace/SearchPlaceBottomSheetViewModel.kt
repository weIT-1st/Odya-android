package com.weit.presentation.ui.searchplace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.follow.ExperiencedFriendInfo
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.usecase.follow.GetCachedFollowerUseCase
import com.weit.domain.usecase.follow.GetExperiencedFriendNumUseCase
import com.weit.domain.usecase.follow.GetExperiencedFriendUseCase
import com.weit.domain.usecase.place.GetPlaceReviewByPlaceIdUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.placereview.EditPlaceReviewViewModel
import com.weit.presentation.util.PlaceReviewContentData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchPlaceBottomSheetViewModel @AssistedInject constructor(
    private val getExperiencedFriendUseCase: GetExperiencedFriendUseCase,
    private val getExperiencedFriendNumUseCase: GetExperiencedFriendNumUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    @Assisted private val placeId: String
) : ViewModel() {

    var experiencedFriendNum = initExperiencedFriendNum
    var friendProfile = ArrayList<ExperiencedFriendInfo>()

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): SearchPlaceBottomSheetViewModel
    }

    init {
        viewModelScope.launch {
            initExperiencedFriendNum()
            initExperiencedFriendProfile()
        }
    }

    private suspend fun initExperiencedFriendNum() {
        val result = getExperiencedFriendNumUseCase(placeId)
        if (result.isSuccess) {
            val num = result.getOrNull()
            if (num != null) {
                experiencedFriendNum = num
            }
        }
    }

    private suspend fun initExperiencedFriendProfile() {
        val result = getExperiencedFriendUseCase(placeId)
        if (result.isSuccess) {
            val profile = result.getOrNull()
            if (profile != null) {
                when (profile.size) {
                    0 -> {}
                    1 -> {
                        friendProfile.add(profile[0])
                    }
                    2 -> {
                        friendProfile.add(profile[0])
                        friendProfile.add(profile[1])
                    }
                    else -> {
                        friendProfile.add(profile[0])
                        friendProfile.add(profile[1])
                        friendProfile.add(profile[2])
                    }
                }
            }
        }
    }


    companion object {
        const val initExperiencedFriendNum = 0
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

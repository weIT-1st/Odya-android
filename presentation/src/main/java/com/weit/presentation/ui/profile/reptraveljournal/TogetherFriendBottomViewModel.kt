package com.weit.presentation.ui.profile.reptraveljournal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.presentation.ui.profile.otherprofile.OtherProfileViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TogetherFriendBottomViewModel @AssistedInject constructor(
    @Assisted val friends: List<TravelJournalCompanionsInfo>,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    ): ViewModel(){

    private val _journalFriends = MutableStateFlow<List<TravelJournalCompanionsInfo>>(emptyList())
    val journalFriends: StateFlow<List<TravelJournalCompanionsInfo>> get() = _journalFriends

    @AssistedFactory
    interface TravelJournalFriendsFactory {
        fun create(friends: List<TravelJournalCompanionsInfo>) : TogetherFriendBottomViewModel
    }

    init{
        viewModelScope.launch {
            _journalFriends.emit(friends)
        }
    }

    fun onFollowStateChange(friend : TravelJournalCompanionsInfo) {
        viewModelScope.launch {
            val currentFollowState = friend.isFollowing
            val result = changeFollowStateUseCase(friend.userId, !currentFollowState)
            if (result.isSuccess) {
                val newFriends = _journalFriends.value.map {
                    if (friend.userId == it.userId) {
                        it.copy(isFollowing = !currentFollowState)
                    } else {
                        it
                    }
                }
                _journalFriends.emit(newFriends)
            } else {
                //
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalFriendsFactory,
            friends: List<TravelJournalCompanionsInfo>
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(friends) as T
            }
        }
    }
}

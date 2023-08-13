package com.weit.presentation.ui.post.travelfriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.usecase.follow.GetInfiniteFollowerUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.toDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class TravelFriendViewModel @Inject constructor(
    getUserIdUseCase: GetUserIdUseCase,
    getInfiniteFollowerUseCase: GetInfiniteFollowerUseCase,
) : ViewModel() {

    private val friends = CopyOnWriteArrayList<FollowUserContent>()
    private val followers = CopyOnWriteArrayList<FollowUserContent>()

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            val id = getUserIdUseCase()
            val page = getInfiniteFollowerUseCase(
                FollowerSearchInfo(
                    userId = id,
                    page = 0,
                    size = 20,
                ),
            ).getOrThrow().content
            followers.addAll(page)
            updateTravelFriends()
        }
        // 전체 팔로워 초기화
    }

    fun initTravelFriends(travelFriends: List<FollowUserContent>) {
        Logger.t("MainTest").i(travelFriends.joinToString("\n"))
        friends.run {
            clear()
            addAll(travelFriends)
        }
        updateTravelFriends()
    }

    fun onAddFriend(position: Int) {
        if (friends.contains(followers[position]).not()) {
            friends.add(followers[position])
            updateTravelFriends()
        }
    }

    fun onRemoveFriend(position: Int) {
        friends.removeAt(position)
        updateTravelFriends()
    }

    fun onSearch(nickname: String) {
        // 팔로워 검색해서 followers 갱신
    }

    fun onComplete() {
        val friendsDTO = friends.map { it.toDTO() }
        viewModelScope.launch {
            _event.emit(Event.OnComplete(friendsDTO))
        }
    }

    private fun updateTravelFriends() {
        viewModelScope.launch {
            _event.emit(Event.OnChangeTravelFriends(friends.toList(), followers.toList()))
        }
    }

    sealed class Event {
        data class OnChangeTravelFriends(
            val travelFriends: List<FollowUserContent>,
            val followers: List<FollowUserContent>,
        ) : Event()
        data class OnComplete(
            val travelFriends: List<FollowUserContentDTO>,
        ) : Event()
    }
}

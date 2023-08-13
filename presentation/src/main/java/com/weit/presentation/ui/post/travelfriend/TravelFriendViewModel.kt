package com.weit.presentation.ui.post.travelfriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.usecase.follow.GetCachedFollowerUseCase
import com.weit.domain.usecase.follow.GetInfiniteFollowerUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.toDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class TravelFriendViewModel @Inject constructor(
    getUserIdUseCase: GetUserIdUseCase,
    private val getInfiniteFollowerUseCase: GetInfiniteFollowerUseCase,
    private val getCachedFollowerUseCase: GetCachedFollowerUseCase,
) : ViewModel() {

    // userId는 반드시 초기화가 선행되어야 함
    private var userId: Long = -1
    private val friends = CopyOnWriteArrayList<FollowUserContent>()
    private var followerPage = 0

    val query = MutableStateFlow("")
    private val _followers = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val followers: StateFlow<List<FollowUserContent>> get() = _followers
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var pageJob: Job = Job().apply {
        complete()
    }

    init {
        viewModelScope.launch {
            userId = getUserIdUseCase()
            updateTravelFriends()
        }
        loadNextFollowers(followerPage, query.value)
    }

    fun onNextFollowers() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextFollowers(followerPage, query.value)
    }

    private fun loadNextFollowers(page: Int, query: String) {
        pageJob = viewModelScope.launch {
            val result = getInfiniteFollowerUseCase(
                FollowerSearchInfo(
                    userId = userId,
                    page = page,
                    size = DEFAULT_PAGE_SIZE,
                ),
                query,
            )
            if (result.isSuccess) {
                val newFollowers = result.getOrThrow()
                followerPage = page + 1
                if (newFollowers.isEmpty()) {
                    loadNextFollowers(page + 1, query)
                }
                _followers.emit(followers.value + newFollowers)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun initTravelFriends(travelFriends: List<FollowUserContent>) {
        friends.run {
            clear()
            addAll(travelFriends)
        }
        viewModelScope.launch {
            query.emit("")
            _followers.emit(_followers.value.toList())
        }
        updateTravelFriends()
    }

    fun onAddFriend(follower: FollowUserContent) {
        if (friends.find { it.userId == follower.userId } == null) {
            friends.add(follower)
            updateTravelFriends()
        }
    }

    fun onRemoveFriend(position: Int) {
        friends.removeAt(position)
        updateTravelFriends()
    }

    fun onSearch(query: String) {
        if (followers.value.isNotEmpty()) {
            pageJob.cancel()
        }
        viewModelScope.launch {
            val filteredFollowers = getCachedFollowerUseCase(query)
            _followers.emit(filteredFollowers)
        }
    }

    fun onComplete() {
        val friendsDTO = friends.map { it.toDTO() }
        viewModelScope.launch {
            _event.emit(Event.OnComplete(friendsDTO))
        }
    }

    private fun updateTravelFriends() {
        viewModelScope.launch {
            _event.emit(Event.OnChangeTravelFriends(friends.toList()))
        }
    }

    sealed class Event {
        data class OnChangeTravelFriends(
            val travelFriends: List<FollowUserContent>,
        ) : Event()
        data class OnComplete(
            val travelFriends: List<FollowUserContentDTO>,
        ) : Event()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}

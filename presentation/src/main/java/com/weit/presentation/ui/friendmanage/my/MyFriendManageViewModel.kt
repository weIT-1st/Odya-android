package com.weit.presentation.ui.friendmanage.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.domain.usecase.follow.DeleteFollowerUseCase
import com.weit.domain.usecase.follow.GetFollowersUseCase
import com.weit.domain.usecase.follow.GetFollowingsUseCase
import com.weit.domain.usecase.follow.SearchFollowersUseCase
import com.weit.domain.usecase.follow.SearchFollowingsUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.presentation.model.Follow
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class MyFriendManageViewModel @Inject constructor(
    getUserIdUseCase: GetUserIdUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingsUseCase: GetFollowingsUseCase,
    private val searchFollowersUseCase: SearchFollowersUseCase,
    private val searchFollowingsUseCase: SearchFollowingsUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
    private val deleteFollowerUseCase: DeleteFollowerUseCase,
) : ViewModel() {

    private val userId: Long = runBlocking {
        getUserIdUseCase()
    }

    val query = MutableStateFlow("")
    private val _searchResultFollowings = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val searchResultFollowings: StateFlow<List<FollowUserContent>> get() = _searchResultFollowings

    private val _searchResultFollowers = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val searchResultFollowers: StateFlow<List<FollowUserContent>> get() = _searchResultFollowers

    private val _defaultFollowers = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val defaultFollowers: StateFlow<List<FollowUserContent>> get() = _defaultFollowers

    private val _defaultFollowings = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val defaultFollowings: StateFlow<List<FollowUserContent>> get() = _defaultFollowings
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var followState: Follow = Follow.FOLLOWER

    private var searchFollowingLastId: Long? = null
    private var searchFollowerLastId: Long? = null
    private val defaultFollowingPage = AtomicInteger(0)
    private val defaultFollowerPage = AtomicInteger(0)


    private var searchJob: Job = Job().apply {
        complete()
    }
    private var defaultJob: Job = Job().apply {
        complete()
    }

    init {
        initData()
    }

    fun initData(){
        searchFollowingLastId = null
        searchFollowerLastId = null
        defaultFollowingPage.set(0)
        defaultFollowerPage.set(0)
        query.value = ""
        _searchResultFollowings.value = emptyList()
        _searchResultFollowers.value = emptyList()
        _defaultFollowers.value = emptyList()
        _defaultFollowings.value = emptyList()
        updateDefaultRv()
        onDefault()
    }


    fun selectFollowMenu(follow: Follow) {
        followState = follow
        initData()
    }

    fun updateSearchRv() {
        viewModelScope.launch {
            _event.emit(Event.OnUpdateSearchRv(followState))
        }
    }

    fun updateDefaultRv() {
        viewModelScope.launch {
            _event.emit(Event.OnUpdateDefaultRv(followState))
        }
    }

    //검색시
    fun onSearch(text: String) {
        viewModelScope.launch {
            searchJob.cancel()
            if (followState == Follow.FOLLOWING) {
                _searchResultFollowings.value = emptyList()
            } else {
                _searchResultFollowers.value = emptyList()
            }
            loadNextSearchFollowersAndFollowings(text, null)
        }
    }

    fun onSearchMoreUser() {
        if (searchJob.isCompleted.not()) {
            return
        }
        if (followState == Follow.FOLLOWING) {
            loadNextSearchFollowersAndFollowings(query.value, searchFollowingLastId)
        } else {
            loadNextSearchFollowersAndFollowings(query.value, searchFollowerLastId)
        }
    }

    private fun loadNextSearchFollowersAndFollowings(query: String, userId: Long?) {
        searchJob = viewModelScope.launch {
            val result = if (followState == Follow.FOLLOWING) {
                searchFollowingsUseCase(SearchFollowRequestInfo(DEFAULT_PAGE_SIZE, userId, query))
            }else{
                searchFollowersUseCase(SearchFollowRequestInfo(DEFAULT_PAGE_SIZE, userId, query))
            }
            if (result.isSuccess) {
                val newFollowersOrFollowings = result.getOrThrow()
                val lastId = newFollowersOrFollowings.lastOrNull()?.userId

                if (followState == Follow.FOLLOWING) {
                    searchFollowingLastId = lastId
                    _searchResultFollowings.emit(searchResultFollowings.value + newFollowersOrFollowings)
                } else {
                    searchFollowerLastId = lastId
                    _searchResultFollowers.emit(searchResultFollowers.value + newFollowersOrFollowings)
                }
            } else {

            }
        }
    }

    //기존
    fun onDefault() {
        if (defaultJob.isCompleted.not()) {
            return
        }
        loadNextDefaultFollowersAndFollowings()
    }

    private fun loadNextDefaultFollowersAndFollowings() {
        defaultJob = viewModelScope.launch {
            val result = if (followState == Follow.FOLLOWING) {
                getFollowingsUseCase(
                    FollowingSearchInfo(
                        userId = userId,
                        page = defaultFollowingPage.get(),
                        size = DEFAULT_PAGE_SIZE,
                    )
                )
            }else{
                getFollowersUseCase(
                    FollowerSearchInfo(
                        userId = userId,
                        page = defaultFollowerPage.get(),
                        size = DEFAULT_PAGE_SIZE,
                    )
                )
            }

            if (result.isSuccess) {
                val newFollowingsOrFollowers = result.getOrThrow()
                if (followState == Follow.FOLLOWING){
                    defaultFollowingPage.incrementAndGet()
                    _defaultFollowings.emit(defaultFollowings.value + newFollowingsOrFollowers)
                } else {
                    defaultFollowerPage.incrementAndGet()
                    _defaultFollowers.emit(defaultFollowers.value + newFollowingsOrFollowers)
                }
            } else{

            }
        }
    }

    fun selectSearchFollowing(friend: FollowUserContent) {
        viewModelScope.launch {
             val result =  deleteFollowUseCase(FollowFollowingIdInfo(friend.userId))

            if (result.isSuccess) {
                val newResult = _searchResultFollowings.value.filterNot { it.userId == friend.userId }
                _searchResultFollowings.emit(newResult)
            } else {
            }
        }
    }

    fun selectDefaultFollowing(friend: FollowUserContent) {
        viewModelScope.launch {
            val result =  deleteFollowUseCase(FollowFollowingIdInfo(friend.userId))

            if (result.isSuccess) {
                val newResult = _defaultFollowings.value.filterNot { it.userId == friend.userId }
                _defaultFollowings.emit(newResult)
            } else {
            }
        }
    }

    fun selectSearchFollower(friend: FollowUserContent) {
        viewModelScope.launch {
            val result =  deleteFollowerUseCase(friend.userId)

            if (result.isSuccess) {
                val newResult = _searchResultFollowers.value.filterNot { it.userId == friend.userId }
                _searchResultFollowers.emit(newResult)
            } else {
            }
        }
    }

    fun selectDefaultFollower(friend: FollowUserContent) {
        viewModelScope.launch {
            val result =  deleteFollowerUseCase(friend.userId)

            if (result.isSuccess) {
                val newResult = _defaultFollowers.value.filterNot { it.userId == friend.userId }
                _defaultFollowers.emit(newResult)
            } else {
            }
        }
    }


    sealed class Event {
        data class OnUpdateSearchRv(
            val followState: Follow,
        ) : Event()

        data class OnUpdateDefaultRv(
            val followState: Follow,
        ) : Event()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}

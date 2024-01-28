package com.weit.presentation.ui.friendmanage.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.FollowerSearchInfo
import com.weit.domain.model.follow.FollowingSearchInfo
import com.weit.domain.model.follow.SearchFollowRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.usecase.follow.CreateFollowCreateUseCase
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.domain.usecase.follow.DeleteFollowerUseCase
import com.weit.domain.usecase.follow.GetCachedFollowerUseCase
import com.weit.domain.usecase.follow.GetFollowersUseCase
import com.weit.domain.usecase.follow.GetFollowingsUseCase
import com.weit.domain.usecase.follow.GetInfiniteFollowerUseCase
import com.weit.domain.usecase.follow.GetInfiniteFollowingUseCase
import com.weit.domain.usecase.follow.OtherSearchFollowersUseCase
import com.weit.domain.usecase.follow.OtherSearchFollowingsUseCase
import com.weit.domain.usecase.follow.SearchFollowersUseCase
import com.weit.domain.usecase.follow.SearchFollowingsUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.domain.usecase.userinfo.GetUsernameUseCase
import com.weit.presentation.model.Follow
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.user.FollowUserContentImpl
import com.weit.presentation.ui.feed.post.FeedPostViewModel
import com.weit.presentation.ui.post.travelfriend.TravelFriendViewModel
import com.weit.presentation.ui.profile.otherprofile.OtherProfileViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class OtherFriendManageViewModel @AssistedInject constructor(
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingsUseCase: GetFollowingsUseCase,
    private val searchFollowersUseCase: OtherSearchFollowersUseCase,
    private val searchFollowingsUseCase: OtherSearchFollowingsUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
    private val createFollowCreateUseCase: CreateFollowCreateUseCase,
    private val getUserUseCase: GetUserUseCase,
    @Assisted private val userId: Long,
) : ViewModel() {

    @AssistedFactory
    interface OtherFriendManageFactory {
        fun create(userId: Long): OtherFriendManageViewModel
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
    private val _event = MutableEventFlow<OtherFriendManageViewModel.Event>()
    val event = _event.asEventFlow()

    private var followState: Follow = Follow.FOLLOWER

    private var searchFollowingLastId: Long? = null
    private var searchFollowerLastId: Long? = null
    private val defaultFollowingPage = AtomicInteger(0)
    private val defaultFollowerPage = AtomicInteger(0)
    private lateinit var user : User


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
        viewModelScope.launch {
            getUserUseCase().onSuccess { userInfo ->
                user = userInfo
            }
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

    private fun loadNextSearchFollowersAndFollowings(query: String, lastUserId: Long?) {
        searchJob = viewModelScope.launch {
            val result = if (followState == Follow.FOLLOWING) {
                searchFollowingsUseCase(userId, SearchFollowRequestInfo(DEFAULT_PAGE_SIZE, lastUserId, query))
            }else{
                searchFollowersUseCase(userId, SearchFollowRequestInfo(DEFAULT_PAGE_SIZE, lastUserId, query))
            }
            if (result.isSuccess) {
                val newFollowersOrFollowings = result.getOrThrow().filterNot {
                    it.nickname == user.nickname
                }
                val lastId = if (newFollowersOrFollowings.isNotEmpty()) newFollowersOrFollowings.last().userId else null

                if (followState == Follow.FOLLOWING) {
                    searchFollowingLastId = lastId
                    _searchResultFollowings.emit(searchResultFollowings.value + newFollowersOrFollowings)
                } else {
                    searchFollowerLastId = lastId
                    _searchResultFollowers.emit(searchResultFollowers.value + newFollowersOrFollowings)
                }
            } else {
                Logger.t("MainTest").i("${result.exceptionOrNull()}")

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
                val newFollowingsOrFollowers = result.getOrThrow().filterNot {
                    it.nickname == user.nickname
                }
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

    fun createFollow(friend: FollowUserContent,type:String){
        viewModelScope.launch {
            val result = createFollowCreateUseCase(FollowFollowingIdInfo(friend.userId))
            if (result.isSuccess) {
                when(type){
                    SEARCH_FOLLOWER -> {
                        val newResult = searchResultFollowers.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    true
                                )
                            }else{
                                it
                            }
                        }
                        _searchResultFollowers.emit(newResult)
                    }
                    SEARCH_FOLLOWING -> {
                        val newResult = searchResultFollowings.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    true
                                )
                            }else{
                                it
                            }
                        }
                        _searchResultFollowings.emit(newResult)
                    }
                    DEFAULT_FOLLOWER -> {
                        val newResult = defaultFollowers.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    true
                                )
                            }else{
                                it
                            }
                        }
                        _defaultFollowers.emit(newResult)
                    }
                    DEFAULT_FOLLOWING -> {
                        val newResult = defaultFollowings.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    true
                                )
                            }else{
                                it
                            }
                        }
                        _defaultFollowings.emit(newResult)
                    }
                }
            } else {
            }
        }
    }

    fun deleteFollow(friend: FollowUserContent, type: String){
        viewModelScope.launch {
            val result = deleteFollowUseCase(FollowFollowingIdInfo(friend.userId))
            if (result.isSuccess) {
                when(type){
                    SEARCH_FOLLOWER -> {
                        val newResult = searchResultFollowers.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    false
                                )
                            }else{
                                it
                            }
                        }
                        _searchResultFollowers.emit(newResult)
                    }
                    SEARCH_FOLLOWING -> {
                        val newResult = searchResultFollowings.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    false
                                )
                            }else{
                                it
                            }
                        }
                        _searchResultFollowings.emit(newResult)
                    }
                    DEFAULT_FOLLOWER -> {
                        val newResult = defaultFollowers.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    false
                                )
                            }else{
                                it
                            }
                        }
                        _defaultFollowers.emit(newResult)
                    }
                    DEFAULT_FOLLOWING -> {
                        val newResult = defaultFollowings.value.map{
                            if(it.userId == friend.userId){
                                FollowUserContentImpl(
                                    it.userId,
                                    it.nickname,
                                    it.profile,
                                    false
                                )
                            }else{
                                it
                            }
                        }
                        _defaultFollowings.emit(newResult)
                    }
                }
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
        const val SEARCH_FOLLOWER = "SEARCH_FOLLOWER"
        const val SEARCH_FOLLOWING = "SEARCH_FOLLOWING"
        const val DEFAULT_FOLLOWER = "DEFAULT_FOLLOWER"
        const val DEFAULT_FOLLOWING = "DEFAULT_FOLLOWING"

        fun provideFactory(
            assistedFactory: OtherFriendManageViewModel.OtherFriendManageFactory,
            userId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }
}

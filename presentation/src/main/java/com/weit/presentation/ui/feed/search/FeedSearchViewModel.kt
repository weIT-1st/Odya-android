package com.weit.presentation.ui.feed.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.search.UserProfileColorInfo
import com.weit.domain.model.user.search.UserProfileInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.usecase.user.DeleteUserSearchUseCase
import com.weit.domain.usecase.user.GetUserSearchUseCase
import com.weit.domain.usecase.user.InsertUserSearchUseCase
import com.weit.domain.usecase.user.SearchUserUseCase
import com.weit.presentation.ui.util.Constants.DEFAULT_DATA_SIZE
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedSearchViewModel @Inject constructor(
    private val searchUserUseCase: SearchUserUseCase,
    private val insertUserSearchUseCase: InsertUserSearchUseCase,
    private val getUserSearchUseCase: GetUserSearchUseCase,
    private val deleteUserSearchUseCase: DeleteUserSearchUseCase,
) : ViewModel() {

    private val _recentSearchUsers = MutableStateFlow<List<UserSearchInfo>>(emptyList())
    val recentSearchUsers: StateFlow<List<UserSearchInfo>> get() = _recentSearchUsers

    private val _searchResultUsers = MutableStateFlow<List<SearchUserContent>>(emptyList())
    val searchResultUsers: StateFlow<List<SearchUserContent>> get() = _searchResultUsers

    val query = MutableStateFlow("")

    private var searchJob: Job = Job().apply {
        complete()
    }

    private var userLastId: Long? = null

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getRecentUserSearch()
    }

    fun onSelectUser(user: SearchUserContent) {
        viewModelScope.launch {
            val newSearchInfo = UserSearchInfo(user.userId,user.nickname,
                UserProfileInfo(user.profile.url,
                    user.profile.color?.let {
                        UserProfileColorInfo(
                            it.colorHex,
                            it.red,
                            it.green,
                            it.blue)
                    }
                )
            )
            insertUserSearchUseCase(newSearchInfo)
            _event.emit(Event.MoveToProfile(user.nickname))
        }
    }

    fun deleteRecentUserSearch(user: UserSearchInfo) {
        viewModelScope.launch {
            val list = recentSearchUsers.value
            val newList = list.filterNot { it == user }
            deleteUserSearchUseCase(user.userId)
            _recentSearchUsers.emit(newList)
        }
    }


    private fun getRecentUserSearch() {
        viewModelScope.launch {
            val result = getUserSearchUseCase()
            _recentSearchUsers.emit(result)

        }
    }

    fun onSearchUser(text: String){
        searchJob.cancel()
        _searchResultUsers.value = emptyList()
        loadNextUsers(text,null)

    }

    fun onNextUsers() {
        if(searchJob.isCompleted.not()){
            return
        }
        loadNextUsers(query.value,userLastId)
    }

    private fun loadNextUsers(query: String, userId: Long?) {
        searchJob = viewModelScope.launch {

            val result = searchUserUseCase(
                SearchUserRequestInfo(DEFAULT_DATA_SIZE, userId, query))

            if (result.isSuccess) {
                val newUsers = result.getOrThrow()

                if (newUsers.isNotEmpty()) {
                    userLastId = newUsers.last().userId
                }
                _searchResultUsers.emit(_searchResultUsers.value + newUsers)
            }
        }
    }

    sealed class Event {
        data class MoveToProfile(val userName: String) : Event()
    }

}

package com.weit.presentation.ui.profile.otherprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.favoritePlace.FriendFavoritePlaceInfo
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalRequest
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.usecase.bookmark.CreateJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.DeleteJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.GetMyJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.GetUserJournalBookMarkUseCase
import com.weit.domain.usecase.favoritePlace.DeleteFavoritePlaceUseCase
import com.weit.domain.usecase.favoritePlace.GetFriendFavoritePlaceCountUseCase
import com.weit.domain.usecase.favoritePlace.GetFriendFavoritePlacesUseCase
import com.weit.domain.usecase.favoritePlace.RegisterFavoritePlaceUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.repjournal.GetOtherRepTravelJournalListUseCase
import com.weit.domain.usecase.user.GetUserLifeshotUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.domain.usecase.user.SearchUserUseCase
import com.weit.presentation.model.profile.lifeshot.FriendProfileUserInfo
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceEntity
import com.weit.presentation.ui.profile.otherprofile.favoriteplace.OtherFavoritePlaceEntity
import com.weit.presentation.ui.util.Constants
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtherProfileViewModel @AssistedInject constructor(
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val getUserLifeshotUseCase: GetUserLifeshotUseCase,
    private val getFriendFavoritePlacesUseCase: GetFriendFavoritePlacesUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val registerFavoritePlaceUseCase: RegisterFavoritePlaceUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase,
    private val getFriendFavoritePlaceCountUseCase: GetFriendFavoritePlaceCountUseCase,
    private val getOtherRepTravelJournalListUseCase: GetOtherRepTravelJournalListUseCase,
    private val getUserJournalBookMarkUseCase: GetUserJournalBookMarkUseCase,
    private val createJournalBookMarkUseCase: CreateJournalBookMarkUseCase,
    private val deleteJournalBookMarkUseCase: DeleteJournalBookMarkUseCase,
    @Assisted private val userName: String,
) : ViewModel() {

    @AssistedFactory
    interface OtherProfileFactory {
        fun create(userName: String): OtherProfileViewModel
    }


    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val _bookMarkTravelJournals = MutableStateFlow<List<JournalBookMarkInfo>>(emptyList())
    val bookMarkTravelJournals: StateFlow<List<JournalBookMarkInfo>> get() = _bookMarkTravelJournals

    private val _repTravelJournal = MutableStateFlow<RepTravelJournalListInfo?>(null)
    val repTravelJournal: StateFlow<RepTravelJournalListInfo?> get() = _repTravelJournal
    private val _favoritePlaces = MutableStateFlow<List<OtherFavoritePlaceEntity>>(emptyList())
    val favoritePlaces: StateFlow<List<OtherFavoritePlaceEntity>> get() = _favoritePlaces

    private val _favoritePlaceCount = MutableStateFlow<Int>(0)
    val favoritePlaceCount: StateFlow<Int> get() = _favoritePlaceCount

    private val _lifeshots = MutableStateFlow<List<UserImageResponseInfo>>(emptyList())
    val lifeshots: StateFlow<List<UserImageResponseInfo>> get() = _lifeshots


    private val _userInfo = MutableStateFlow<FriendProfileUserInfo?>(null)
    val userInfo: StateFlow<FriendProfileUserInfo?> get() = _userInfo

    private val _followState = MutableStateFlow<Boolean>(false)
    val followState: StateFlow<Boolean> get() = _followState

    private lateinit var user: SearchUserContent

    private var lifeShotJob: Job = Job().apply {
        complete()
    }
    private var lastImageId: Long? = null

    private var repTravelJournalJob: Job = Job().apply {
        complete()
    }
    private var lastRepTravelJournalId: Long? = null

    private var bookMarkLastId: Long? = null
    private var bookMarkPageJob: Job = Job().apply {
        complete()
    }

    init {
        initData()
    }

    fun initData() {
        viewModelScope.launch {
            lastImageId = null
            _lifeshots.value = emptyList()
            lastRepTravelJournalId = null
            _repTravelJournal.value = null
            getUserInfo()
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            val result = searchUserUseCase(
                SearchUserRequestInfo(null, null, userName)
            )
            if (result.isSuccess) {
                val newUsers = result.getOrThrow()
                if (newUsers.isNotEmpty()) {
                    user = newUsers.first()
                    _followState.emit(user.isFollowing)
                    getUserStatistics()
                    onNextLifeShots()
                    loadFavoritePlaces()
                    getFavoritePlaceCount()
                    loadNextRepTravelJournals()
                    onNextBookMarkJournal()

                }
            }
        }
    }

    private fun getFavoritePlaceCount() {
        viewModelScope.launch {
            val result = getFriendFavoritePlaceCountUseCase(user.userId)
            if (result.isSuccess) {
                _favoritePlaceCount.emit(result.getOrThrow())
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())

            }
        }
    }


    private fun getUserStatistics() {
        viewModelScope.launch {
            val result = getUserStatisticsUseCase(user.userId)
            if (result.isSuccess) {
                _userInfo.emit(FriendProfileUserInfo(user, result.getOrThrow()))
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun onNextLifeShots() {
        if (lifeShotJob.isCompleted.not()) {
            return
        }
        loadNextLifeShots()
    }

    private fun loadNextLifeShots() {
        lifeShotJob = viewModelScope.launch {
            val result = getUserLifeshotUseCase(
                LifeshotRequestInfo(Constants.DEFAULT_DATA_SIZE, lastImageId, user.userId)
            )
            if (result.isSuccess) {
                val newLifeShots = result.getOrThrow()
                newLifeShots.lastOrNull()?.let {
                    lastImageId = it.imageId
                    _lifeshots.emit(lifeshots.value + newLifeShots)
                }
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())

            }
        }
    }

    private fun loadFavoritePlaces() {
        viewModelScope.launch {
            val result = getFriendFavoritePlacesUseCase(
                FriendFavoritePlaceInfo(user.userId)
            )
            if (result.isSuccess) {
                _favoritePlaces.value = emptyList()
                val newFavoritePlaces = result.getOrThrow().map {
                    val placeDetail = getPlaceDetailUseCase(it.placeId)
                    OtherFavoritePlaceEntity(
                        it.favoritePlaceId,
                        it.placeId,
                        placeDetail.name,
                        placeDetail.address,
                        it.isFavoritePlace
                    )
                }
                newFavoritePlaces.lastOrNull()?.let {
                    _favoritePlaces.emit(_favoritePlaces.value + newFavoritePlaces)
                }
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun selectFavoritePlace(place: OtherFavoritePlaceEntity) {
        viewModelScope.launch {
            val result = if (place.isFavoritePlace) {
                deleteFavoritePlaceUseCase(place.favoritePlaceId)
            } else {
                registerFavoritePlaceUseCase(place.placeId)
            }

            if (result.isSuccess) {
                val newPlaces = _favoritePlaces.value.map {
                    if (place.favoritePlaceId == it.favoritePlaceId) {
                        it.copy(isFavoritePlace = !place.isFavoritePlace)
                    } else {
                        it
                    }
                }
                _favoritePlaces.emit(newPlaces)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun onFollowStateChange() {
        viewModelScope.launch {
            val currentFollowState = _followState.value
            val result = changeFollowStateUseCase(user.userId, !currentFollowState)
            if (result.isSuccess) {
                _followState.emit(!currentFollowState)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }


    private fun loadNextRepTravelJournals() {
      viewModelScope.launch {
            val result = getOtherRepTravelJournalListUseCase(
                RepTravelJournalRequest(Constants.DEFAULT_DATA_SIZE, lastRepTravelJournalId),
                user.userId
            )
            if (result.isSuccess) {
                val newRepTravelJournal = result.getOrThrow().first()
                _repTravelJournal.emit(newRepTravelJournal)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())

            }
        }
    }

    fun onNextBookMarkJournal() {
        if (bookMarkPageJob.isCompleted.not()) {
            return
        }
        loadNextBookMarkJournals()
    }

    private fun loadNextBookMarkJournals() {
        bookMarkPageJob = viewModelScope.launch {
            val result =
                getUserJournalBookMarkUseCase(user.userId, null, lastId = bookMarkLastId, null)

            if (result.isSuccess) {
                val newJournals = result.getOrThrow()
                if (newJournals.isNotEmpty()) {
                    bookMarkLastId = newJournals.last().travelJournalBookMarkId
                }
                _bookMarkTravelJournals.emit(bookMarkTravelJournals.value + newJournals)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun updateBookmarkTravelJournalBookmarkState(travelJournal: JournalBookMarkInfo) {
        viewModelScope.launch {
            val result = if (travelJournal.isBookmarked) {
                deleteJournalBookMarkUseCase(travelJournal.travelJournalId)
            } else {
                createJournalBookMarkUseCase(travelJournal.travelJournalId)
            }

            if (result.isSuccess) {
                val newJournals = _bookMarkTravelJournals.value.map {
                    if (travelJournal.travelJournalBookMarkId == it.travelJournalBookMarkId) {
                        it.copy(isBookmarked = !travelJournal.isBookmarked)
                    } else {
                        it
                    }
                }
                _bookMarkTravelJournals.emit(newJournals)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {

        data class GetUserStatisticsSuccess(
            val statistics: UserStatistics,
            val user: SearchUserContent
        ) : Event()

        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: OtherProfileFactory,
            userName: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userName) as T
            }
        }
    }
}


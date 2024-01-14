package com.weit.presentation.ui.profile.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.favoritePlace.FavoritePlaceInfo
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalListInfo
import com.weit.domain.model.reptraveljournal.RepTravelJournalRequest
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.usecase.favoritePlace.DeleteFavoritePlaceUseCase
import com.weit.domain.usecase.favoritePlace.GetFavoritePlaceCountUseCase
import com.weit.domain.usecase.favoritePlace.GetFavoritePlacesUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.repjournal.GetMyRepTravelJournalListUseCase
import com.weit.domain.usecase.user.GetUserLifeshotUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.profile.lifeshot.LifeShotImageDetailDTO
import com.weit.presentation.model.profile.lifeshot.ProfileUserInfo
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceEntity
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
class MyProfileViewModel @Inject constructor(
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserLifeshotUseCase: GetUserLifeshotUseCase,
    private val getFavoritePlacesUseCase: GetFavoritePlacesUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase,
    private val getFavoritePlaceCountUseCase: GetFavoritePlaceCountUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val getMyRepTravelJournalListUseCase: GetMyRepTravelJournalListUseCase,
 ) : ViewModel() {

    private val _repTravelJournals = MutableStateFlow<List<RepTravelJournalListInfo>>(emptyList())
    val repTravelJournals: StateFlow<List<RepTravelJournalListInfo>> get() = _repTravelJournals

    private val _favoritePlaces = MutableStateFlow<List<FavoritePlaceEntity>>(emptyList())
    val favoritePlaces: StateFlow<List<FavoritePlaceEntity>> get() = _favoritePlaces

    private val _favoritePlaceCount = MutableStateFlow<Int>(0)
    val favoritePlaceCount: StateFlow<Int> get() = _favoritePlaceCount

    private val _lifeshots = MutableStateFlow<List<UserImageResponseInfo>>(emptyList())
    val lifeshots: StateFlow<List<UserImageResponseInfo>> get() = _lifeshots

    private val _userProfile = MutableStateFlow<String?>(null)
    val userProfile: StateFlow<String?> get() = _userProfile

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val _userInfo = MutableStateFlow<ProfileUserInfo?>(null)
    val userInfo: StateFlow<ProfileUserInfo?> get() = _userInfo

    private lateinit var user : User

    private var lifeShotJob: Job = Job().apply {
        complete()
    }
    private var lastImageId: Long? = null

    private var repTravelJournalJob: Job = Job().apply {
        complete()
    }
    private var lastRepTravelJournalId: Long? = null

    fun initData(){
        viewModelScope.launch {
            lastImageId = null
            _lifeshots.value = emptyList()
            lastRepTravelJournalId = null
            _repTravelJournals.value = emptyList()
            getUserUseCase().onSuccess {
                user = it
                getUserStatistics()
                onNextLifeShots()
                loadFavoritePlaces()
                getFavoritePlaceCount()
                onNextRepTravelJournals()
            }
        }
    }

    fun getUserProfileNone(){
        viewModelScope.launch {
            val result = getUserUseCase()
            if (result.isSuccess) {
                _userProfile.emit(result.getOrThrow().profile.url)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun getUserStatistics() {
        viewModelScope.launch {
            val result = getUserStatisticsUseCase(user.userId)
            if (result.isSuccess) {
                _userInfo.emit(ProfileUserInfo(user,result.getOrThrow()))
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
                LifeshotRequestInfo(DEFAULT_DATA_SIZE, lastImageId, user.userId)
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

    fun selectLifeShot(lifeShotEntity: UserImageResponseInfo, position: Int) {
        viewModelScope.launch {
            val lifeShots = _lifeshots.value.map {
                LifeShotImageDetailDTO(
                    it.imageId,
                    it.imageUrl,
                    it.placeId,
                    it.isLifeShot,
                    it.placeName,
                    it.journalId,
                    it.communityId
                )
            }
            _event.emit(Event.OnSelectLifeShot(lifeShots, position,lastImageId,user.userId))
        }
    }

    private fun getFavoritePlaceCount(){
        viewModelScope.launch {
            val result = getFavoritePlaceCountUseCase()
            if (result.isSuccess) {
                _favoritePlaceCount.emit(result.getOrThrow())
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())

            }
        }
    }

    private fun loadFavoritePlaces() {
        viewModelScope.launch {
            val result = getFavoritePlacesUseCase(
                FavoritePlaceInfo()
            )
            if (result.isSuccess) {
                _favoritePlaces.value = emptyList()
                val newFavoritePlaces = result.getOrThrow().map{
                    val placeDetail = getPlaceDetailUseCase(it.placeId)
                    FavoritePlaceEntity(it.favoritePlaceId,it.placeId,placeDetail.name,placeDetail.address)
                }
                newFavoritePlaces.lastOrNull()?.let {
                    _favoritePlaces.emit(_favoritePlaces.value + newFavoritePlaces)
                }
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun deleteFavoritePlace(place: FavoritePlaceEntity){
        viewModelScope.launch {
            val result = deleteFavoritePlaceUseCase(
                place.favoritePlaceId
            )
            if (result.isSuccess) {
                getFavoritePlaceCount()
                loadFavoritePlaces()
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun onNextRepTravelJournals() {
        if (repTravelJournalJob.isCompleted.not()) {
            return
        }
        loadNextRepTravelJournals()
    }

    private fun loadNextRepTravelJournals() {
        repTravelJournalJob = viewModelScope.launch {
            val result = getMyRepTravelJournalListUseCase(
                RepTravelJournalRequest(DEFAULT_DATA_SIZE, lastRepTravelJournalId)
            )
            if (result.isSuccess) {
                val newRepTravelJournals = result.getOrThrow()
                newRepTravelJournals.lastOrNull()?.let {
                    lastRepTravelJournalId = it.repTravelJournalId
                    _repTravelJournals.emit(repTravelJournals.value + newRepTravelJournals)
                }
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

        data class OnSelectLifeShot(
            val lifeshots: List<LifeShotImageDetailDTO>,
            val position: Int,
            val lastImageId: Long?,
            val userId: Long,
        ) : Event()

        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }

}


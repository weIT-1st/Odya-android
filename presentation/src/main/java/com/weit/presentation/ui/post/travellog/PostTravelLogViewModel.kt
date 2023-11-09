package com.weit.presentation.ui.post.travellog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.model.post.place.SelectPlaceDTO
import com.weit.presentation.model.post.travellog.DailyTravelLog
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.post.travellog.toDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class PostTravelLogViewModel @Inject constructor() : ViewModel() {

    val title = MutableStateFlow("")

    private val dailyTravelLogs = CopyOnWriteArrayList<DailyTravelLog>().apply {
        add(DailyTravelLog(day = 1))
    }
    private val friends = CopyOnWriteArrayList<FollowUserContent>()

    private val _travelPeriod = MutableStateFlow(TravelPeriod())
    val travelPeriod: StateFlow<TravelPeriod> get() = _travelPeriod

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val _changeTravelLogEvent = MutableEventFlow<List<DailyTravelLog>>()
    val changeTravelLogEvent = _changeTravelLogEvent.asEventFlow()

    fun initViewState(travelFriends: List<FollowUserContent>?, selectPlace: SelectPlaceDTO?) {
        updateDailyTravelLogs()
        travelFriends?.let {
            initTravelFriends(it)
        }
        selectPlace?.let {
            updateSelectPlace(it)
        }
    }

    private fun initTravelFriends(travelFriends: List<FollowUserContent>) {
        friends.run {
            clear()
            addAll(travelFriends)
        }
        val friendsSummary = travelFriends
            .slice(0 until DEFAULT_FRIENDS_SUMMARY_COUNT)
            .map { it.profile }
        val remainingFriendsCount = travelFriends.size - friendsSummary.size
        viewModelScope.launch {
            _event.emit(Event.OnChangeTravelFriends(friendsSummary, remainingFriendsCount))
        }
    }

    fun onAddDailyTravelLog() {
        dailyTravelLogs.add(DailyTravelLog(day = dailyTravelLogs.size + 1))
        updateDailyTravelLogs()
    }

    fun onDeleteDailyTravelLog(position: Int) {
        dailyTravelLogs.removeAt(position)
        val newLogs = dailyTravelLogs.mapIndexed { index, dailyTravelLog ->
            dailyTravelLog.copy(day = index + 1)
        }
        dailyTravelLogs.clear()
        dailyTravelLogs.addAll(newLogs)
        updateDailyTravelLogs()
    }

    fun onSelectPictures(position: Int, pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
            val target = dailyTravelLogs.removeAt(position)
            dailyTravelLogs.add(
                position,
                target.copy(images = target.images + images),
            )
            updateDailyTravelLogs()
        }
    }

    fun onSelectPlace(position: Int) {
        // 장소 빼오기
        val imagePlaces = emptyList<PlacePredictionDTO>()
        viewModelScope.launch {
            _event.emit(Event.OnSelectPlace(imagePlaces, position))
        }
    }

    fun onEditTravelFriends() {
        viewModelScope.launch {
            val friendsDTO = friends.map { it.toDTO() }
            _event.emit(Event.OnEditTravelFriends(friendsDTO))
        }
    }

    fun onDeletePicture(position: Int, imageIndex: Int) {
        val target = dailyTravelLogs.removeAt(position)
        val newTargetImages = target.images.toMutableList().apply {
            removeAt(imageIndex)
        }
        dailyTravelLogs.add(
            position,
            target.copy(images = newTargetImages),
        )
    }

    private fun updateDailyTravelLogs() {
        viewModelScope.launch {
            _changeTravelLogEvent.emit(dailyTravelLogs.toList())
        }
    }

    private fun updateSelectPlace(selectPlace: SelectPlaceDTO) {
        val log = dailyTravelLogs.removeAt(selectPlace.position).copy(place = selectPlace.toPlacePrediction())
        dailyTravelLogs.add(selectPlace.position, log)
        updateDailyTravelLogs()
    }

    private fun SelectPlaceDTO.toPlacePrediction(): PlacePrediction {
        return PlacePrediction(
            placeId = placeId,
            address = address,
            name = name,
        )
    }

    fun showDatePicker() {
        viewModelScope.launch {
            _event.emit(Event.ShowDatePicker(travelPeriod.value))
        }
    }

    fun onChangePeriod(period: TravelPeriod) {
        viewModelScope.launch {
            _travelPeriod.emit(period)
            _event.emit(Event.ClearDatePickerDialog)
        }
    }

    fun onPost() {
    }

    sealed class Event {
        data class OnChangeTravelFriends(
            val friendsSummary: List<UserProfile>,
            val remainingFriendsCount: Int,
        ) : Event()
        data class OnEditTravelFriends(
            val travelFriends: List<FollowUserContentDTO>,
        ) : Event()

        data class OnSelectPlace(
            val imagePlaces: List<PlacePredictionDTO>,
            val dailyTravelLogPosition: Int,
        ) : Event()

        data class ShowDatePicker(
            val currentPeriod: TravelPeriod,
        ) : Event()

        object ClearDatePickerDialog : Event()
    }

    companion object {
        private const val DEFAULT_FRIENDS_SUMMARY_COUNT = 3
    }
}

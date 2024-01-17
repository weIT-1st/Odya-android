package com.weit.presentation.ui.post.travellog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.journal.TravelJournalContentRequest
import com.weit.domain.model.journal.TravelJournalRegistrationInfo
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.journal.RegisterTravelJournalUseCase
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.model.post.place.SelectPlaceDTO
import com.weit.presentation.model.post.travellog.DailyTravelLog
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.post.travellog.toDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.ui.util.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class PostTravelLogViewModel @Inject constructor(
    private val registerTravelJournalUseCase: RegisterTravelJournalUseCase
) : ViewModel() {

    val title = MutableStateFlow("")

    private val friends = CopyOnWriteArrayList<FollowUserContent>()

    private val _travelFriendsInfo = MutableStateFlow(TravelFriendsInfo())
    val travelFriendsInfo: StateFlow<TravelFriendsInfo> get() = _travelFriendsInfo

    private val _travelPeriod = MutableStateFlow(TravelPeriod())
    val travelPeriod: StateFlow<TravelPeriod> get() = _travelPeriod

    private val _visibility = MutableStateFlow(Visibility.PUBLIC)
    val visibility: StateFlow<Visibility> get() = _visibility

    private val _dailyTravelLogs = MutableStateFlow(listOf(DailyTravelLog(day = 1)))
    val dailyTravelLogs: StateFlow<List<DailyTravelLog>> get() = _dailyTravelLogs

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun initViewState(travelFriends: List<FollowUserContent>?, selectPlace: SelectPlaceDTO?) {
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

        val friendsSummary = if (travelFriends.size >= DEFAULT_FRIENDS_SUMMARY_COUNT) {
            travelFriends
                .slice(0 until min(DEFAULT_FRIENDS_SUMMARY_COUNT, travelFriends.size))
                .map { it.profile }
        } else {
            travelFriends
                .map { it.profile }
        }

        val remainingFriendsCount = travelFriends.size - friendsSummary.size
        viewModelScope.launch {
            _travelFriendsInfo.emit(TravelFriendsInfo(friendsSummary, remainingFriendsCount))
        }
    }

    fun onAddDailyTravelLog() {
        val currentLogs = _dailyTravelLogs.value
        updateDailyTravelLogs(currentLogs.plus(DailyTravelLog(day = currentLogs.size + 1)))
    }

    fun onDeleteDailyTravelLog(position: Int) {
        val currentLogs = _dailyTravelLogs.value.toMutableList().apply {
            removeAt(position)
        }
        val newLogs = currentLogs.mapIndexed { index, dailyTravelLog ->
            dailyTravelLog.copy(day = index + 1)
        }
        updateDailyTravelLogs(newLogs)
    }

    fun onSelectPictures(position: Int, pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
            val updatedLogs = dailyTravelLogs.value.toMutableList()
            val target = updatedLogs.removeAt(position)
            updatedLogs.add(
                position,
                target.copy(images = target.images + images),
            )
            updateDailyTravelLogs(updatedLogs)
        }
    }

    private fun updateDailyTravelLogs(logs: List<DailyTravelLog>) {
        viewModelScope.launch {
            _dailyTravelLogs.emit(logs)
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
        val updatedLogs = dailyTravelLogs.value.toMutableList()
        val target = updatedLogs.removeAt(position)
        val newTargetImages = target.images.toMutableList().apply {
            removeAt(imageIndex)
        }
        updatedLogs.add(
            position,
            target.copy(images = newTargetImages),
        )
        updateDailyTravelLogs(updatedLogs)
    }

    private fun updateSelectPlace(selectPlace: SelectPlaceDTO) {
        val updatedLogs = dailyTravelLogs.value.toMutableList()
        val log =
            updatedLogs.removeAt(selectPlace.position).copy(place = selectPlace.toPlacePrediction())
        updatedLogs.add(selectPlace.position, log)
        updateDailyTravelLogs(updatedLogs)
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
        }
    }

    fun onDatePickerDismissed() {
        viewModelScope.launch {
            _event.emit(Event.ClearDatePickerDialog)
        }
    }

    fun selectTravelLogVisibility(selectedVisibility: Visibility) {
        viewModelScope.launch {
            _visibility.emit(selectedVisibility)
        }
    }

    fun onPost() {
        viewModelScope.launch {
            val journalTitle: String = title.value
            val date = travelPeriod.value
            val start: List<Int> =
                listOf(date.start.year, date.start.monthValue, date.start.dayOfMonth)
            val end: List<Int> = listOf(date.end.year, date.end.monthValue, date.end.dayOfMonth)
            val visibility = visibility.value.name
            val companionIds: List<Long> = friends.map { it.userId }
            val travelCompanionNames: List<String> = friends.map { it.nickname }
            val dailyTravelLog = dailyTravelLogs.value

            val travelJournalRegistration = TravelJournalRegistrationInfo(
                title = journalTitle,
                travelStartDate = start,
                travelEndDate = end,
                visibility = visibility,
                travelCompanionIds = companionIds,
                travelCompanionNames = travelCompanionNames,
                travelJournalContentRequests = dailyTravelLog.map { log ->
                    TravelJournalContentRequest(
                        content = log.contents,
                        placeId = log.place?.placeId,
                        latitudes = emptyList(),
                        longitudes = emptyList(),
                        travelDate = if (log.date == null) {
                            noInputDate(log.day)
                            return@launch
                        } else {
                            listOf(log.date.year, log.date.monthValue, log.date.dayOfMonth)
                        },
                        contentImageNames = log.images.map {
                            it.split("/").last() + IMAGE_EXTENSION_WEBP
                        }
                    )
                },
                travelDurationDays = dailyTravelLog.count(),
                contentImageNameTotalCount = dailyTravelLog.sumOf { it.images.count() }
            )
            val travelJournalImages = emptyList<String>().toMutableList()

            for (log in dailyTravelLog) {
                log.images.forEach { image ->
                    travelJournalImages.add(image)
                }
            }

            val result = registerTravelJournalUseCase(
                travelJournalRegistrationInfo = travelJournalRegistration,
                travelJournalImages = travelJournalImages
            )

            if (result.isSuccess) {
                _event.emit(Event.SuccessPostJournal)
            } else {
                //todo 에러처리
                Log.d("register travel", "Register Travel Journal Fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun onPickDailyDate(position: Int) {
        viewModelScope.launch {
            val date = dailyTravelLogs.value[position].date
            val minDateMillis = getMinDateMillis(position)
            val maxDateMillis = travelPeriod.value.end.toMillis()
            _event.emit(Event.ShowDailyDatePicker(date, minDateMillis, maxDateMillis, position))
        }
    }

    fun onDailyDateSelected(position: Int, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val updatedLogs = dailyTravelLogs.value.toMutableList()
        val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        val target = updatedLogs.removeAt(position).copy(date = selectedDate)
        updatedLogs.add(position, target)
        updateDailyTravelLogs(updatedLogs)
    }

    private fun getMinDateMillis(position: Int): Long {
        val minDate =
            dailyTravelLogs.value.getOrNull(position - 1)?.date ?: travelPeriod.value.start
        return minDate.toMillis()
    }

    private fun noInputDate(day : Int) {
        viewModelScope.launch {
            _event.emit(Event.NoDateInLog(day))
        }
    }

    sealed class Event {
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

        data class ShowDailyDatePicker(
            val currentDate: LocalDate?,
            val minDateMillis: Long,
            val maxDateMillis: Long,
            val position: Int,
        ) : Event()

        object ClearDatePickerDialog : Event()
        object SuccessPostJournal : Event()
        data class NoDateInLog(
            val day: Int
        ) : Event()
    }

    data class TravelFriendsInfo(
        val friendsSummary: List<UserProfile> = emptyList(),
        val remainingFriendsCount: Int = 0,
    )

    companion object {
        private const val DEFAULT_FRIENDS_SUMMARY_COUNT = 3
        private const val IMAGE_EXTENSION_WEBP = ".webp"
    }
}

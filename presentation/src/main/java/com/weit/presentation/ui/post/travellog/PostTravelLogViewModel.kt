package com.weit.presentation.ui.post.travellog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.model.post.travellog.DailyTravelLog
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class PostTravelLogViewModel @Inject constructor() : ViewModel() {

    val title = MutableStateFlow("")

    private val dailyTravelLogs = CopyOnWriteArrayList<DailyTravelLog>().apply {
        add(DailyTravelLog(day = 1))
    }

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        updateDailyTravelLogs()
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
            _event.emit(Event.OnChangeTravelLog(dailyTravelLogs.toList()))
        }
    }

    fun onPost() {
    }

    sealed class Event {
        data class OnChangeTravelLog(val logs: List<DailyTravelLog>) : Event()
    }
}

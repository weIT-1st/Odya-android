package com.weit.presentation.ui.post.datepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class DatePickerViewModel : ViewModel() {

    private val _period = MutableStateFlow(TravelPeriod())
    val period: StateFlow<TravelPeriod> get() = _period

    private val _entity = MutableStateFlow(
        DatePickerEntity(0, period.value.start.toMillis(), period.value.start, CalenderType.START)
    )
    val entity: StateFlow<DatePickerEntity> get() = _entity

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun initTravelPeriod(travelPeriod: TravelPeriod) {
        viewModelScope.launch {
            _period.emit(travelPeriod)
            val entity = DatePickerEntity(0, travelPeriod.end.toMillis(), travelPeriod.start, CalenderType.START)
            _entity.emit(entity)
        }
    }

    fun onSelectDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        viewModelScope.launch {
            when (entity.value.type) {
                CalenderType.START -> {
                    _period.emit(period.value.copy(start = selectedDate))
                }
                CalenderType.END -> {
                    _period.emit(period.value.copy(end = selectedDate))
                }
            }
        }
    }

    fun onChangeCalenderType(type: CalenderType) {
        viewModelScope.launch {
            val date = period.value
            val entity = when (type) {
                CalenderType.START -> {
                    DatePickerEntity(0, date.end.toMillis(), date.start, type)
                }
                CalenderType.END -> {
                    DatePickerEntity(date.start.toMillis(), System.currentTimeMillis(), date.end, type)
                }
            }
            _entity.emit(entity)
        }
    }

    fun onDismiss() {
        viewModelScope.launch {
            _event.emit(Event.OnDismiss)
        }
    }

    fun onComplete() {
        viewModelScope.launch {
            _event.emit(Event.OnComplete(period.value))
        }
    }

    private fun LocalDate.toMillis(): Long {
        return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    sealed class Event {
        object OnDismiss : Event()
        data class OnComplete(val travelPeriod: TravelPeriod) : Event()
    }

    data class DatePickerEntity(
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val currentDate: LocalDate,
        val type: CalenderType,
    )
}
